package com.example.hatalk.signalRoom.sigRoom.socket

import android.content.Context
import com.example.hatalk.signalRoom.sigRoom.FirstContent
import com.example.hatalk.signalRoom.sigRoom.IntroContentsResponse
import com.example.hatalk.signalRoom.sigRoom.SignalRoomTimer
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*
import android.util.Log

class ContentsReadySocket(
    private val context: Context,
    private val groupRoomName: String,
    private val myGender: String,
    private val myIcon: String
) {
    private lateinit var signalRoomTimer: SignalRoomTimer
    private lateinit var socket: Socket
    private val onContentsConnect = Emitter.Listener { args ->
        emitListener(args)
    }

    fun set() {
        try {
            socket = ContentsSocketApplication.get()
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace();
        }
    }

    fun makeOn() {
        socket.on("${groupRoomName}firstChoice", onContentsConnect)
    }

    fun emit(firstContent: FirstContent) {
        val firstContentGson = Gson()
        val firstContentObj = JSONObject(firstContentGson.toJson(firstContent))

        socket.emit("readyToServer", firstContentObj)
    }

    fun disconnect() {
        socket.disconnect()
    }

    private fun emitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
        val introContentsResponse =
            Gson().fromJson(res.toString(), IntroContentsResponse::class.java)

        val introductionTime = Date(introContentsResponse.contentsStartTime.introduction).time
        val firstChoiceTime = Date(introContentsResponse.contentsStartTime.firstChoice).time

        signalRoomTimer = SignalRoomTimer(context, introductionTime, firstChoiceTime, myIcon)

        signalRoomTimer.checkIntroTime()
        signalRoomTimer.checkIntroFirstTime()
        signalRoomTimer.checkIntroSecondTime()

        if (myGender == "0") {
            signalRoomTimer.setFirstChoiceForMan()
        } else if (myGender == "1") {
            signalRoomTimer.setFirstChoiceForWoman()
        }

        signalRoomTimer.checkFirstChoiceTime()
    }
}