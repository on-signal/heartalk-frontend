package com.heartsignal.hatalk.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.FragmentProfileEditBinding
import com.heartsignal.hatalk.main.userModel.UserModel


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


    }
}