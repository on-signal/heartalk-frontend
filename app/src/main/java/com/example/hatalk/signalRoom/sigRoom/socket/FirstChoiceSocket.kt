package com.example.hatalk.signalRoom.sigRoom.socket

import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class FirstChoiceSocket(
    private val groupName: String,
    private val userId: String,
    private val gender: String,
    private val choice: String
) {
    private lateinit var socket: Socket
    private val onAnswerToQuestionConnect = Emitter.Listener {
        args -> emitListener(args)
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
        socket.on("${groupName}answerToQuestion", onAnswerToQuestionConnect)
    }

    private fun emitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
    }
}