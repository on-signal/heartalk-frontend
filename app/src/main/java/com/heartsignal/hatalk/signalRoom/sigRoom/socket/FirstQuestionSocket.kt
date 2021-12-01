package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.signalRoom.sigRoom.FirstAnswerRequest
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class FirstQuestionSocket(
    private val context: Context,
    private val groupName: String,
    private val myId: String,
    private val myGender: String,
    private val question: String,
) {
    private lateinit var socket: Socket
    private val dialogBuilder = AlertDialog.Builder(context)
    private val onFirstQuestionConnect = Emitter.Listener { _ ->
        emitListener()
    }

    fun set() {
        try {
            socket = ContentsSocketApplication.get()
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun makeOn() {
        socket.on("${groupName}FirstQuestion", onFirstQuestionConnect)
    }

    private fun emitListener() {
        if (myGender == "0") {
            renderingForMan()
        }
    }

    private fun renderingForMan() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.first_question_dialog, null)
        dialogBuilder.setView(dialogView).setTitle(question).setCancelable(false)
            .setPositiveButton("확인") { _, _ ->
                val answer = dialogView.findViewById<EditText>(R.id.first_question_answer).text
                Toast.makeText(context, "$answer", Toast.LENGTH_LONG).show()
                val gson = Gson()
                val firstAnswer =
                    JSONObject(gson.toJson(FirstAnswerRequest(groupName, myId, answer.toString())))
                socket.emit("answerToQuestionToServer", firstAnswer)
            }

        Thread {
            UiThreadUtil.runOnUiThread(Runnable {
                kotlin.run {
                    dialogBuilder.show()
                }
            })
        }.start()
    }

    private fun renderingForWoman() {}

    fun disconnect() {
        socket.disconnect()
    }
}