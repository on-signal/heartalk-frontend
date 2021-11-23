package com.example.hatalk

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.hatalk.databinding.FragmentMainHomeBinding
import com.example.hatalk.main.HomeActivity
import com.example.hatalk.model.UserJoinModel
import com.example.hatalk.network.MatchingApi
import com.example.hatalk.network.MatchingConfirmRequest
import com.example.hatalk.network.MatchingConfirmResponse
import com.example.hatalk.network.MatchingRequest
import com.example.hatalk.signalRoom.PRIVATE.IDs
import com.example.hatalk.signalRoom.sigRoom.SignalRoomActivity
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_main_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.jar.Manifest
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
    private val sharedViewModel: UserJoinModel by activityViewModels()

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


        binding?.kakaoLogoutButton?.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(context, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding?.kakaoUnlinkButton?.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(context, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                }
            }
        }
        /**
         * [CometChat로그인] [Matching]
         */
        val cometLogin = binding?.cometLogin
        val apiKey: String = IDs.APIKEY

        cometLogin?.setOnClickListener {
            val UID: String = binding?.loginEdit?.text.toString()
            CometChat.login(UID, apiKey, object : CometChat.CallbackListener<User>() {
                override fun onSuccess(p0: User?) {
                    Log.d(TAG, "Login Successful : " + p0?.toString())
                }

                override fun onError(p0: CometChatException?) {
                    Log.d(TAG, "Login failed with exception: " + p0?.message)
                }
            })
        }

        val initCall = binding?.initiateCall
        initCall?.setOnClickListener {
            matchingCall()
            matchConfirm()
//            goToSigRoom()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    private fun matchingCall() {
        val matchingRequest = MatchingRequest(
            binding?.loginEdit?.text.toString(),
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
            binding?.loginEdit?.text.toString()
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