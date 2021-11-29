package com.heartsignal.hatalk.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.FragmentMainHomeBinding
import com.heartsignal.hatalk.main.data.MatchingConfirmData
import com.heartsignal.hatalk.main.data.MatchingConfirmResponse
import com.heartsignal.hatalk.main.data.MatchingSocketApplication
import com.heartsignal.hatalk.main.data.MatchingStartData
import com.heartsignal.hatalk.main.userModel.UserModel
import com.heartsignal.hatalk.signalRoom.PRIVATE.IDs
import com.heartsignal.hatalk.signalRoom.sigRoom.SignalRoomActivity
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_signal_room.view.*
import kotlinx.android.synthetic.main.fragment_main_home.*
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [MainHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainHomeFragment : Fragment() {
    private val TAG = "HEART"


    private var binding: FragmentMainHomeBinding? = null
    private val sharedViewModel: UserModel by activityViewModels()
    lateinit var mSocket: Socket

    private lateinit var GUID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMainHomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
            mainHomeFragment = this@MainHomeFragment
        }

        try {
            mSocket = MatchingSocketApplication.get()
            mSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace();
        }

        Glide.with(this)
            .load(sharedViewModel.photoUrl)
            .into(binding!!.homeProfileImage)


        /** [CometChat로그인] */
        cometchatLogin()

        /** [MatchingButton] */
        val matchingButton = binding?.matchingButton
        matchingButton?.setOnClickListener {
//            goToSigRoom()
            matching()
        }


    }



    private fun cometchatLogin() {
        val apiKey: String = IDs.APIKEY
        val UID: String = sharedViewModel.kakaoUserId
        CometChat.login(UID, apiKey, object : CometChat.CallbackListener<User>() {
            override fun onSuccess(p0: User?) {
                Log.d(TAG, "Login Successful : " + p0?.toString())
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Login failed with exception: " + p0?.message)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mSocket.disconnect()
    }




    private fun matching() {

        val userId = sharedViewModel.kakaoUserId

        val matchingStartMessage = MatchingStartData(
            userId,
            sharedViewModel.nickname,
            sharedViewModel.gender
        )
        Log.d(TAG, "here is matchingButton onClick")
        val gson = Gson()
        val matchingObj = JSONObject(gson.toJson(matchingStartMessage))
        val onMatchingConnect = Emitter.Listener { args ->
            Log.d(TAG, args[0].toString())
            val matchingResponse = JSONObject(args[0].toString())
            GUID = matchingResponse.getString("groupName")
            //팝업창을 띄우기
            val matchingConfirmSuccessMessage = MatchingConfirmData(
                userId,
                GUID,
                true
            )
            val matchingConfirmFailMessage = MatchingConfirmData(
                userId,
                GUID,
                false
            )
            val matchingConfirmSuccessObj = JSONObject(gson.toJson(matchingConfirmSuccessMessage))
            val matchingConfirmFailObj = JSONObject(gson.toJson(matchingConfirmFailMessage))
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setTitle("매칭")
                .setMessage("매칭을 수락하시겠습니까?")
                .setPositiveButton("수락",
                    DialogInterface.OnClickListener{ dialog, id ->
                        mSocket.emit("imready", matchingConfirmSuccessObj)
                })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener{ dialog, id ->
                        mSocket.emit("imready", matchingConfirmFailObj)
                    }
                )
            Thread {
                runOnUiThread(Runnable {
                    kotlin.run {
                        dialogBuilder.show()
                    }
                })
            }.start()
        }
        val onMatchingConfirmConnect = Emitter.Listener { args ->
            val matchingConfirmJSON = JSONObject(args[0].toString())
            val matchingConfirmResponse = Gson().fromJson(matchingConfirmJSON.toString(), MatchingConfirmResponse::class.java)
            Log.d(TAG, matchingConfirmResponse.toString())
            CometChat.joinGroup(matchingConfirmResponse.groupName,
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "",
                object : CometChat.CallbackListener<Group>() {
                    override fun onSuccess(p0: Group?) {
                        Log.d(TAG, p0.toString())
                    }

                    override fun onError(p0: CometChatException?) {
                        Log.d(
                            TAG,
                            "Group joining failed with exception: " + p0?.message
                        )
                    }
                })

            activity?.let {
                val intent = Intent(it, SignalRoomActivity::class.java)
                intent.putExtra("matchingData", matchingConfirmResponse)
                it.startActivity(intent)
            }
        }

        mSocket.on("ready-${userId}", onMatchingConnect)
        mSocket.on("${userId}", onMatchingConfirmConnect)
        mSocket.emit("matchstart", matchingObj)

        /**
         * [Socket IO Chat End]
         */
    }


}