package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.heartsignal.hatalk.databinding.FirstAnswerDialogBinding
import com.heartsignal.hatalk.model.sigRoom.AnswerModel

class FirstAnswerFragmentDialog: DialogFragment() {
    private lateinit var binding: FirstAnswerDialogBinding
    private val answerModel: AnswerModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FirstAnswerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = answerModel
            lifecycleOwner = viewLifecycleOwner
            firstAnswerDialogFragment = this@FirstAnswerFragmentDialog
        }
    }
}