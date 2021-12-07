package com.heartsignal.hatalk.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.FragmentProfileEditBinding
import com.heartsignal.hatalk.main.userModel.UserModel
import com.heartsignal.hatalk.network.UpdateUserRequest
import com.heartsignal.hatalk.network.UpdateUserResponse
import com.heartsignal.hatalk.network.UserApi
import com.heartsignal.hatalk.signalRoom.sigRoom.SignalRoomActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileEditFragment : Fragment() {
    private val TAG = "HEART"
    private var binding: FragmentProfileEditBinding? = null
    private val sharedViewModel: UserModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
            profileEditFragment = this@ProfileEditFragment
        }

        val editButton: Button? = binding?.editButton
        editButton?.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val tempNickname = binding?.textInputEditNickname?.text.toString()
                val tempEmail = binding?.textInputEditEmail?.text.toString()
                val tempPhoneNumber = binding?.textInputEditPhoneNumber?.text.toString()
                Log.d(TAG, tempNickname)
                Log.d(TAG, tempEmail)
                Log.d(TAG, tempPhoneNumber)
                val updateUserRequest = UpdateUserRequest(
                    sharedViewModel.kakaoUserId,
                    tempNickname,
                    tempEmail,
                    tempPhoneNumber
                )
                Log.d(TAG, sharedViewModel.accessToken)
                val updateUserResponse: Response<UpdateUserResponse> = UserApi.retrofitService.updateUser("Bearer ${sharedViewModel.accessToken}", updateUserRequest)
                Log.d(TAG, updateUserResponse.body()?.msg.toString())
                if (updateUserResponse.body()?.msg == "success") {
                    sharedViewModel.setNickname(tempNickname)
                    sharedViewModel.setEmail(tempEmail)
                    sharedViewModel.setPhoneNumber(tempPhoneNumber)

                    findNavController().navigate(R.id.action_profileEditFragment_to_mainHomeFragment)
                }

            }
        }




    }
}