package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.content.Context
import android.widget.Toast
import com.heartsignal.hatalk.signalRoom.sigRoom.FirstContent
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class ContentsReadySocket(private val context: Context, private val groupName: String) {

    private lateinit var socket: Socket
    private val onContentsReadyConnect = Emitter.Listener { _ ->
        emitListener()
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
        socket.on("${groupName}Start", onContentsReadyConnect)
    }

    fun emit(firstContent: FirstContent) {
        val firstContentGson = Gson()
        val firstContentObj = JSONObject(firstContentGson.toJson(firstContent))

        socket.emit("readyToServer", firstContentObj)
    }

    fun disconnect() {
        socket.disconnect()
    }

    private fun emitListener() {
        Thread {
            UiThreadUtil.runOnUiThread(Runnable {
                Toast.makeText(
                    context,
                    "자기 소개 시간입니다.",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }.start()
    }
}