package com.heartsignal.hatalk.main

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
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
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.FragmentMainHomeBinding
import com.heartsignal.hatalk.main.userModel.UserModel
import com.heartsignal.hatalk.signalRoom.PRIVATE.IDs
import com.heartsignal.hatalk.signalRoom.sigRoom.SignalRoomActivity
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.google.gson.Gson
import com.heartsignal.hatalk.GlobalApplication
import com.heartsignal.hatalk.main.data.*
import com.heartsignal.hatalk.model.userInfo
import com.heartsignal.hatalk.signalRoom.PRIVATE.URLs
import io.socket.client.IO
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
    lateinit var activity: Activity

    private var binding: FragmentMainHomeBinding? = null
    private val sharedViewModel: UserModel by activityViewModels()
    lateinit var mSocket: Socket
    lateinit var contentsSocket: Socket
    private var matchingStatus: Boolean = false
    private var matchingConfirmResponse: MatchingConfirmResponse? = null

    private lateinit var GUID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMainHomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        val userInfo: userInfo = GlobalApplication.userInfo

        sharedViewModel.setEmail(userInfo.email)
        sharedViewModel.setName(userInfo.name)
        sharedViewModel.setNickname(userInfo.nickname)
        sharedViewModel.setProfileUrl(userInfo.photoUrl)
        sharedViewModel.setGender(userInfo.gender)
        sharedViewModel.setAge(userInfo.age)
        sharedViewModel.setPhoneNumber(userInfo.phoneNumber)
        sharedViewModel.setKakaoUserId(userInfo.kakaoUserId)
        sharedViewModel.setAccessToken(userInfo.accessToken)

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
            .load(GlobalApplication.userInfo.photoUrl)
            .apply(RequestOptions().circleCrop())
            .into(binding!!.homeProfileImage)


        /** [CometChat로그인] */
        cometchatLogin()

        /** [MatchingButton] */
        val matchingButton = binding?.matchingButton
        matchingButton?.setOnClickListener {
            if (matchingStatus) matchingCancel() else matching(it as Button)
        }

        mSocket.on("ready-${sharedViewModel.kakaoUserId}", onMatchingConnect)
        mSocket.on(sharedViewModel.kakaoUserId, onMatchingConfirmConnect)

        mSocket.on("cancel-${sharedViewModel.kakaoUserId}", onMatchingCancelConnect)


        Log.d("HEART", "${matchingConfirmResponse?.groupName}groupCall")
        mSocket.on("groupCall", onCallStartConnect)

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
//        mSocket.disconnect()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) activity = context
    }


    private fun matchingCancel() {

        val matchingCancelMessage = MatchingCancelData(sharedViewModel.kakaoUserId, sharedViewModel.gender)
        val gson = Gson()
        val matchingCancelObj = JSONObject(gson.toJson(matchingCancelMessage))

        mSocket.emit("cancel", matchingCancelObj)

    }


    private fun matching(button: Button) {
        matchingStatus = true
        button.text = "매칭 중.."


        val userId = sharedViewModel.kakaoUserId

        val matchingStartMessage = MatchingStartData(
            userId,
            sharedViewModel.nickname,
            sharedViewModel.gender
        )


        val gson = Gson()
        val matchingObj = JSONObject(gson.toJson(matchingStartMessage))

        mSocket.emit("matchstart", matchingObj)

        /**
         * [Socket IO Chat End]
         */
    }

    val onMatchingCancelConnect = Emitter.Listener { args ->
        val matchingCancelJSON = JSONObject(args[0].toString())
        val matchingCancelResponse = Gson().fromJson(matchingCancelJSON.toString(), MatchingCancelResponse::class.java)

        if (matchingCancelResponse.msg == "success") {
            matchingStatus = false
            val matchingButton = binding?.matchingButton
            runOnUiThread {
                matchingButton?.text = "매칭하기"
            }
        } else {
            Log.d(TAG, "FAIL CANCEL")
        }
    }

    val onMatchingConfirmConnect = Emitter.Listener { args ->
        val matchingConfirmJSON = JSONObject(args[0].toString())
        matchingConfirmResponse = Gson().fromJson(matchingConfirmJSON.toString(), MatchingConfirmResponse::class.java)
        val groupName = matchingConfirmResponse?.groupName
        if (matchingConfirmResponse?.msg == "success") {
            CometChat.joinGroup(groupName!!,
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "",
                object : CometChat.CallbackListener<Group>() {
                    override fun onSuccess(p0: Group?) {
                        Log.d(TAG, p0.toString())
                        callReadyEmit(groupName)
                    }

                    override fun onError(p0: CometChatException?) {
                        Log.d( TAG,"Group joining failed with exception: " + p0?.message)
                    }
                }
            )
        }
    }

    private val onCallStartConnect = Emitter.Listener { args ->
        val callReadyJSON = JSONObject(args[0].toString())
        val callReadyMsg = Gson().fromJson(callReadyJSON.toString(), CallReadyResponse::class.java)
        if (callReadyMsg.msg == true) {
            activity?.let {
                val intent = Intent(it, SignalRoomActivity::class.java)
                intent.putExtra("matchingData", matchingConfirmResponse)
                it.startActivity(intent)
            }
        }
    }


    val onMatchingConnect = Emitter.Listener { args ->
        val userId = sharedViewModel.kakaoUserId
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
        val gson = Gson()
        val matchingConfirmSuccessObj = JSONObject(gson.toJson(matchingConfirmSuccessMessage))
        val matchingConfirmFailObj = JSONObject(gson.toJson(matchingConfirmFailMessage))


        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle("매칭")
            .setMessage("매칭을 수락하시겠습니까?")
            .setPositiveButton("수락",
                DialogInterface.OnClickListener{ dialog, id ->
                    mSocket.emit("imready", matchingConfirmSuccessObj)
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener{ dialog, id ->
                    mSocket.emit("imready", matchingConfirmFailObj)
                    matchingStatus = false
                    val matchingButton = binding?.matchingButton
                    runOnUiThread {
                        matchingButton?.text = "매칭하기"
                    }
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

    private fun callReadyEmit(groupId: String) {
        val callReadyMsg = CallReadyRequest(groupId, "groupCall")
        val gson = Gson()
        val callReadyObj = JSONObject(gson.toJson(callReadyMsg))
        Log.d(TAG, "emit")

        mSocket.emit("callInit", callReadyObj)
    }
}