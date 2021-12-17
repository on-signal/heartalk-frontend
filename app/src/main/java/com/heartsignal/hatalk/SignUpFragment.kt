package com.heartsignal.hatalk

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.heartsignal.hatalk.databinding.FragmentSignUpBinding
import com.heartsignal.hatalk.model.UserJoinModel
import java.util.regex.Pattern


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

            carrierInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    sharedViewModel.setCarrier(parent?.getItemAtPosition(pos).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
            signup.setOnClickListener {
                onSignUp()
            }
        }

        val phoneNumberInput = binding?.textInputEditPhoneNumber
        phoneNumberInput?.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

     fun onSignUp(){
         val name = binding?.textInputEditName?.text.toString()
         val socialNumber = binding?.textInputEditSocialNumber?.text.toString()
         val phoneNumber = binding?.textInputEditPhoneNumber?.text.toString()
         val nickname = binding?.textInputEditNickname?.text.toString()

         if (checkSocialNumber(socialNumber)) {
             sharedViewModel.setName(name)
             sharedViewModel.setSocialNumber(socialNumber)
             sharedViewModel.setPhoneNumber(phoneNumber)
             sharedViewModel.setNickname(nickname)
             goToNextScreen()
         }
    }

    private fun checkSocialNumber(sNum: String): Boolean {
        if (!Pattern.matches("\\d{6}[1-4]", sNum)) {
            Toast.makeText(context, "주민등록번호 형식을 지켜주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun goToNextScreen() {
        findNavController().navigate(R.id.action_signUpFragment_to_signUpLoadingFragment)
    }

}