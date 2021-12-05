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
import com.view.circulartimerview.CircularTimerListener
import com.view.circulartimerview.CircularTimerView
import com.view.circulartimerview.TimeFormatEnum
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Math.ceil
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
        val animalName = res.getString("name")
        if (talkingIcon != "end") {
            Thread {
                UiThreadUtil.runOnUiThread(Runnable {
                    rotationStartAnimation(talkingIcon)
                    timerSetting()
                })
            }.start()
        }
        when (talkingIcon) {
            myIcon -> {
                callManager.muteAudio(false)
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        introduceNotify(animalName)
                    })
                }.start()
            }

            "end" -> {
                callManager.muteAudio(true)
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        introduceNotify(animalName)
                    })
                }.start()
            }

            else -> {
                callManager.muteAudio(true)
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        introduceNotify(animalName)
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

    private fun timerSetting() {
        val progressBar: CircularTimerView? = view.findViewById(R.id.progress_circular)
        progressBar!!.progress = 0f

        progressBar.setCircularTimerListener(object : CircularTimerListener {
            override fun updateDataOnTick(remainingTimeInMs: Long): String? {
                return ceil((remainingTimeInMs / 1000).toDouble()).toInt().toString()
            }

            override fun onTimerFinished() {
                progressBar.setProgressBackgroundColor("#FF808080")
                progressBar.setTextColor("#FF808080")
            }
        }, 10, TimeFormatEnum.SECONDS, 10)

        progressBar.startTimer()
    }

    private fun introduceNotify(animalName: String) {
        val notificationHeader = view.findViewById<TextView>(R.id.notification_header)
        val nextAnimal = when(animalName) {
            "늑대" -> "여우"
            "여우" -> "펭귄"
            "펭귄" -> "햄스터"
            "햄스터" -> "사자"
            "사자" -> "꿀벌"
            "꿀벌" -> "1대1 음성통화"
            else -> "끝"
        }
        if (nextAnimal == "끝") {
            notificationHeader.text = "${animalName}님 목소리 공개 끝! ${nextAnimal}님 준비해주세요"
        } else {
            notificationHeader.text = "${animalName}님 자기소개 중 입니다. (다음: ${nextAnimal})"
        }
    }

}