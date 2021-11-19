package com.example.hatalk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hatalk.databinding.FragmentSignUpBinding
import com.example.hatalk.model.UserJoinModel


class SignUpFragment : Fragment() {
    private var binding: FragmentSignUpBinding? = null
    private val sharedViewModel: UserJoinModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getString("email", null)?.let { sharedViewModel.setEmail(it) }
        savedInstanceState?.getString("photoUrl", null)?.let { sharedViewModel.setProfileUrl(it) }

        binding?.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
            signupFragment = this@SignUpFragment

            signup.setOnClickListener {
                onSignUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

     fun onSignUp(){
        val name = binding?.textInputEditName?.text.toString()
        val socialNumber = binding?.textInputEditSocialNumber?.text.toString()
        val carrier = binding?.textInputEditCarrier?.text.toString()
        val phoneNumber = binding?.textInputEditPhoneNumber?.text.toString()
        val nickname = binding?.textInputEditNickname?.text.toString()

        sharedViewModel.setName(name)
        sharedViewModel.setSocialNumber(socialNumber)
        sharedViewModel.setCarrier(carrier)
        sharedViewModel.setPhoneNumber(phoneNumber)
        sharedViewModel.setNickname(nickname)
         goToNextScreen()
    }

    private fun goToNextScreen() {
        findNavController().navigate(R.id.action_signUpFragment_to_signUpLoadingFragment)
    }

}