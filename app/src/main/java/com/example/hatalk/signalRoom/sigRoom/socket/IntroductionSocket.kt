package com.example.hatalk.signalRoom.sigRoom.socket

import android.content.Context
import android.widget.Toast
import com.cometchat.pro.core.CallManager
import com.facebook.react.bridge.UiThreadUtil
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class IntroductionSocket(
    private val context: Context,
    private val groupName: String,
    private val myIcon: String
) {
    private lateinit var socket: Socket
    private val onIntroductionConnect = Emitter.Listener { args ->
        emiListener(args)
    }
    private val callManager = CallManager.getInstance()

    fun set() {
        try {
            socket = ContentsSocketApplication.get()
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun makeOn() {
        socket.on("${groupName}Introduction", onIntroductionConnect)
    }

    fun disconnect() {
        socket.disconnect()
    }

    private fun emiListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())

        val talkingIcon = res.getString("icon")

        when (talkingIcon) {
            myIcon -> {
                callManager.muteAudio(false)
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        Toast.makeText(
                            context,
                            "$talkingIcon 소개 시간입니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }.start()
            }

            "end" -> {
                callManager.muteAudio(true)
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        Toast.makeText(
                            context,
                            "자기 소개가 끝났습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }.start()
            }

            else -> {
                callManager.muteAudio(false)
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        Toast.makeText(
                            context,
                            "$talkingIcon 소개 시간입니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }.start()
            }
        }
    }
}