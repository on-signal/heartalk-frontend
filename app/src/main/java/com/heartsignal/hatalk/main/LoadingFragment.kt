package com.heartsignal.hatalk.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.main.userModel.UserModel
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoadingFragment : Fragment() {
    private val TAG = "HEART"
    private val sharedViewModel: UserModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        TimeUnit.SECONDS.sleep(1L)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                findNavController().navigate(R.id.action_loadingFragment_to_mainHomeFragment)
            },
            1000
        )
    }
}