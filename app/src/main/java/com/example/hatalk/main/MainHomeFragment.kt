package com.example.hatalk.main

import android.content.Intent
import android.os.Bundle

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.hatalk.databinding.FragmentMainHomeBinding
import com.example.hatalk.main.userModel.UserModel
import com.example.hatalk.model.UserJoinModel
import com.example.hatalk.model.userInfo
import com.example.hatalk.network.*
import com.example.hatalk.signalRoom.PRIVATE.IDs
import com.example.hatalk.signalRoom.sigRoom.SignalRoomActivity
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_main_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
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
        matchingStatus = false
        Glide.with(this)
            .load(sharedViewModel.photoUrl)
            .into(binding!!.homeProfileImage)


        /**
         * [CometChat로그인] [Matching]
         */

        cometchatLogin()

        val matchingButton = binding?.matchingButton
        matchingButton?.setOnClickListener {
            matchingCall()
            matchConfirm()
//            goToSigRoom()
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
    }


    private fun matchingCall() {
        val matchingRequest = MatchingRequest(
            sharedViewModel.kakaoUserId,
            "뀨"
        )

        CoroutineScope(Dispatchers.Main).launch {
            val matchingResponse = MatchingApi.retrofitService.StartMatch(matchingRequest)
            var timer = Timer()


            if (matchingResponse.isSuccessful) {
                Log.d(TAG, "Matching try success")
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        matchConfirm()

                        if (matchingStatus) {
                            timer.cancel()
                            CometChat.joinGroup(matchingData.group_room_name.toString(),
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
                                intent.putExtra("matchingData", matchingData)
                                it.startActivity(intent)
                            }
                        }
                    }
                }, 100, 300)
            } else {
                Log.d(TAG, "Matching try Fail")
            }
        }
    }

    private fun matchConfirm() {
        val matchingConfirmRequest = MatchingConfirmRequest(
            sharedViewModel.kakaoUserId
        )


        CoroutineScope(Dispatchers.Main).launch {
            val matchConfirmResponse =
                MatchingApi.retrofitService.ConfirmMatch(matchingConfirmRequest)


            if (matchConfirmResponse.body()?.msg.toString() == "success") {
                Log.d(TAG, "Success CONFIRM")
                matchingStatus = true
                matchingData = matchConfirmResponse.body()!!
                Log.d(TAG, matchingData.toString())
            } else {
                Log.d(TAG, "FAIL CONFIRM")
            }
        }
    }
}