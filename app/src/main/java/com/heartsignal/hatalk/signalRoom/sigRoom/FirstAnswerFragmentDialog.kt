package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.heartsignal.hatalk.databinding.FirstAnswerDialogBinding
import com.heartsignal.hatalk.model.sigRoom.AnswerModel
import com.heartsignal.hatalk.model.sigRoom.MatchingModel
import com.heartsignal.hatalk.signalRoom.sigRoom.socket.ContentsSocketApplication
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class FirstAnswerFragmentDialog : DialogFragment() {
    private lateinit var binding: FirstAnswerDialogBinding
    private val matchingModel: MatchingModel by activityViewModels()
    private val answerModel: AnswerModel by activityViewModels()
    private lateinit var firstComeSocket: Socket
    private val onFirstCome = Emitter.Listener { args ->
        firstComeEmitListener(args)
    }

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

        try {
            firstComeSocket = ContentsSocketApplication.get()
            firstComeSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        firstComeSocket.on("${matchingModel.groupName}FirstCome", onFirstCome)
    }

    private fun firstComeEmitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
        Log.d("FirstCome: ", res.toString())
        val answerSelectResponse = Gson().fromJson(res.toString(), AnswerSelectResponse::class.java)
        if (answerSelectResponse.msg == "success") {
            
        } else if(answerSelectResponse.msg == "fail") {

        }
    }

    fun selectFirstOne() {
        val gson = Gson()
        val selection = JSONObject(gson.toJson(AnswerSelectRequest(
            matchingModel.myId,
            answerModel.ownerIdList[0],
            matchingModel.groupName
        )))
        firstComeSocket.emit("answerChoiceToServer", selection)
    }
}