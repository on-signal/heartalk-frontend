package com.example.hatalk.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.hatalk.R
import com.example.hatalk.databinding.FragmentStartBinding


class StartFragment : Fragment() {
    private var binding: FragmentStartBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.startFragment = this
    }

    fun moveToLogin() {
        findNavController().navigate(R.id.action_startFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}