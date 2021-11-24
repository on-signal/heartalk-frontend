package com.example.hatalk.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.hatalk.databinding.FragmentMainHomeBinding
import com.example.hatalk.main.data.MatchingConfirmData
import com.example.hatalk.main.data.MatchingConfirmResponse
import com.example.hatalk.main.data.MatchingSoketApplication
import com.example.hatalk.main.data.MatchingStartData
import com.example.hatalk.main.userModel.UserModel
import com.example.hatalk.signalRoom.PRIVATE.IDs
import com.example.hatalk.signalRoom.sigRoom.SignalRoomActivity
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_signal_room.view.*
import kotlinx.android.synthetic.main.fragment_main_home.*
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 * Use the [MainHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainHomeFragment : Fragment() {
    private val TAG = "HEART"

    private var matchingStatus by Delegates.notNull<Boolean>()
    private lateinit var matchingData: MatchingConfirmResponse

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


        return fragmentBinding.root
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
//            matchingCall()
//            matchConfirm()
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
            val matchingConfirmMessage = MatchingConfirmData(
                userId,
                GUID
            )
            val matchingConfirmObj = JSONObject(gson.toJson(matchingConfirmMessage))
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setTitle("매칭")
                .setMessage("매칭을 수락하시겠습니까?")
                .setPositiveButton("수락",
                    DialogInterface.OnClickListener{ dialog, id ->
                        mSocket.emit("imready", matchingConfirmObj)
                })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener{ dialog, id ->

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
            CometChat.joinGroup(matchingConfirmResponse.groupName.toString(),
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