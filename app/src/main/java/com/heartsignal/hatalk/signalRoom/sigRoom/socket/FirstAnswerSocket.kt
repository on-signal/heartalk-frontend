package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.content.Context
import android.util.Log
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class FirstAnswerSocket(private val context: Context, private val groupName: String) {
    private lateinit var socket: Socket
    private val onFirstAnswer = Emitter.Listener { args ->
        emitListener(args)
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
        socket.on("${groupName}AnswerChoice", onFirstAnswer)
    }

    private fun emitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
        Log.d("FirstAnswerGet: ", res.toString())
    }
}