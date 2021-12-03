package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.cometchat.pro.core.CallManager
import com.facebook.react.bridge.UiThreadUtil
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.signalRoom.sigRoom.SignalRoomActivity
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class IntroductionSocket(
    private val context: Context,
    private val groupName: String,
    private val myIcon: String,
    private val view: View
) {
    private lateinit var socket: Socket
    private val onIntroductionConnect = Emitter.Listener { args ->
        emitListener(args)
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

    private fun emitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())

        val talkingIcon = res.getString("icon")
        if (talkingIcon != "end") {
            Thread {
                UiThreadUtil.runOnUiThread(Runnable {
                    rotationStartAnimation(talkingIcon)
                })
            }.start()
        }
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
                callManager.muteAudio(true)
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

    private fun rotationStartAnimation(icon: String) {
        val myView: TextView = when (icon) {
            "lion" -> view.findViewById(R.id.lion_text)
            "bee" -> view.findViewById(R.id.bee_text)
            "penguin" -> view.findViewById(R.id.penguin_text)
            "hamster" -> view.findViewById(R.id.hamster_text)
            "wolf" -> view.findViewById(R.id.wolf_text)
            else -> view.findViewById(R.id.fox_text)
        }

        myView.animate()
            .rotationX(360f)
            .setDuration(1000)
            .withStartAction { myView.setBackgroundResource(R.drawable.border_background_speaker)}
            .withEndAction { myView.rotationX = 0f }
            .start()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                myView.animate()
                    .rotationX(360f)
                    .setDuration(1000)
                    .withStartAction { myView.setBackgroundResource(R.drawable.border_background)}
                    .withEndAction { myView.rotationX = 0f }
                    .start()
                if (myIcon == icon) {
                    myView.setBackgroundColor(Color.parseColor("#fff7d9"))
                }
            },
            10000
        )
    }

}