package com.example.hatalk.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.hatalk.R
import com.example.hatalk.databinding.FragmentMainHomeBinding
import com.example.hatalk.databinding.FragmentProfileBinding
import com.example.hatalk.main.userModel.UserModel
import com.example.hatalk.model.UserJoinModel
import com.example.hatalk.model.userInfo
import com.kakao.sdk.user.UserApiClient


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private val TAG = "HEART"

    private var binding: FragmentProfileBinding? = null
    private val sharedViewModel: UserModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userInfo = arguments?.getParcelable<userInfo>("userInfo")

        val kakaoLogoutButton: Button? = binding?.kakaoLogoutButton

        kakaoLogoutButton?.setOnClickListener {
            Log.d(TAG, "button good")
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(context, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Log.d(TAG, "HELLO")
        binding?.kakaoLogoutButton?.setOnClickListener {
            Log.d(TAG, "button good")
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(context, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}