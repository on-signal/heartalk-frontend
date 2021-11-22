package com.example.hatalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hatalk.databinding.FragmentMainHomeLoadingBinding
import com.example.hatalk.main.HomeActivity
import com.example.hatalk.model.UserJoinModel
import com.example.hatalk.model.userInfo
import com.example.hatalk.network.LoginRequest
import com.example.hatalk.network.UserApi
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class MainHomeLoadingFragment : Fragment() {
    private var binding: FragmentMainHomeLoadingBinding? = null
    private val sharedViewModel: UserJoinModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMainHomeLoadingBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UserApiClient.instance.me { user, error ->
            sharedViewModel.setEmail(user?.kakaoAccount?.email.toString())
            sharedViewModel.setProfileUrl(user?.kakaoAccount?.profile?.profileImageUrl.toString())
            sharedViewModel.setKakaoUserId(user?.id.toString())

            val loginRequest = LoginRequest(user?.id.toString())

            lifecycleScope.launch {
                val response = UserApi.retrofitService.login(loginRequest)
                sharedViewModel.setAccessToken(response.body()?.accessToken.toString())

                if (response.body()?.accessToken != null) {
                    val getProfileResponse =
                        UserApi.retrofitService.getCurrentUser("Bearer ${response.body()?.accessToken.toString()}")

                    sharedViewModel.setNickname(getProfileResponse.body()?.nickname.toString())
                    sharedViewModel.setName(getProfileResponse.body()?.name.toString())

                    val gender = if (getProfileResponse.body()?.gender == 0) {
                        "남성"
                    } else {
                        "여성"
                    }

                    sharedViewModel.setGender(gender)
                    getProfileResponse.body()?.age?.let { sharedViewModel.setAge(it) }
                    val userInfo = userInfo(
                        sharedViewModel.kakaoUserId,
                        sharedViewModel.email,
                        sharedViewModel.photoUrl,
                        sharedViewModel.name,
                        sharedViewModel.socialNumber,
                        sharedViewModel.carrier,
                        sharedViewModel.phoneNumber,
                        sharedViewModel.nickname,
                        sharedViewModel.accessToken,
                        sharedViewModel.refreshToken,
                        sharedViewModel.gender,
                        sharedViewModel.age
                    )

                    activity?.let {
                        val intent = Intent(it, HomeActivity::class.java)
                        intent.putExtra("userInfo", userInfo)
                        it.startActivity(intent)
//                        startActivity(intent)
                    }
                } else {
                    findNavController().navigate(R.id.action_mainHomeLoadingFragment_to_signUpFragment)
                }
            }
        }
    }
}