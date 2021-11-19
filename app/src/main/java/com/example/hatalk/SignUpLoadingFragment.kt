package com.example.hatalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hatalk.databinding.FragmentSignUpLoadingBinding
import com.example.hatalk.model.UserJoinModel
import com.example.hatalk.network.SignUpRequest
import com.example.hatalk.network.UserApi
import kotlinx.coroutines.launch

class SignUpLoadingFragment : Fragment() {
    private var binding: FragmentSignUpLoadingBinding? = null
    private val sharedViewModel: UserJoinModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSignUpLoadingBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signUpRequest =
            SignUpRequest(
                sharedViewModel.email,
                sharedViewModel.name,
                sharedViewModel.socialNumber,
                sharedViewModel.carrier,
                sharedViewModel.phoneNumber,
                sharedViewModel.nickname,
                sharedViewModel.photoUrl
            )
        lifecycleScope.launch {
            val signUpResponse = UserApi.retrofitService.signUp(signUpRequest)
            sharedViewModel.setAccessToken(signUpResponse.body()?.accessToken.toString())

            val getProfileResponse =
                UserApi.retrofitService.getCurrentUser("Bearer ${signUpResponse.body()?.accessToken.toString()}")

            val gender = if (getProfileResponse.body()?.gender == 0) {
                "남성"
            } else {
                "여성"
            }

            sharedViewModel.setGender(gender)
            getProfileResponse.body()?.age?.let { sharedViewModel.setAge(it) }

            findNavController().navigate(R.id.action_signUpLoadingFragment_to_mainHomeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}