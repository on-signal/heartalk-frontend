package com.example.hatalk.signalRoom.sigRoom

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.cometchat.pro.core.CallManager
import com.facebook.react.bridge.UiThreadUtil
import java.util.*
import kotlin.concurrent.timer

class SignalRoomTimer(
    private val context: Context,
    private val introductionTime: Long,
    private val firstChoiceTime: Long,
    private val myIcon: String
) {
    private val callManager = CallManager.getInstance()
    private val womanIconList = arrayOf("fox")
    private val manIconList = arrayOf("wolf")
    private val dialogBuilder = AlertDialog.Builder(context)
    private lateinit var selectedItem: String

    fun checkIntroTime() {
        timer(period = 10) {
            if (Date().time >= introductionTime) {
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        Toast.makeText(
                            context,
                            "지금부터 자기소개 시간입니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }.start()
                this.cancel()
            }
        }
    }

    fun checkIntroFirstTime() {
        timer(period = 10) {
            if (Date().time >= introductionTime + 5000) {
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        Toast.makeText(
                            context,
                            "늑대님 자기 소개 시간 10초 드리겠습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }.start()
                if (myIcon == "wolf") {
                    callManager.muteAudio(false)
                } else {
                    callManager.muteAudio(true)
                }
                this.cancel()
            }
        }
    }

    fun checkIntroSecondTime() {
        timer(period = 10) {
            if (Date().time >= introductionTime + 15000) {
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        Toast.makeText(
                            context,
                            "여우님 자기 소개 시간 10초 드리겠습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }.start()
                if (myIcon == "fox") {
                    callManager.muteAudio(false)
                } else {
                    callManager.muteAudio(true)
                }
                this.cancel()
            }
        }
    }

    fun setFirstChoiceForMan() {
        dialogBuilder.setTitle("첫 인상 선택")
            .setSingleChoiceItems(womanIconList, -1) { _, pos ->
                selectedItem = womanIconList[pos]
            }.setPositiveButton("OK") { _, _ ->
                Toast.makeText(
                    context,
                    "${selectedItem.toString()} is Selected", Toast.LENGTH_LONG
                ).show();
            }
    }

    fun setFirstChoiceForWoman() {
        dialogBuilder.setTitle("첫 인상 선택")
            .setSingleChoiceItems(manIconList, -1) { _, pos ->
                selectedItem = manIconList[pos]
            }.setPositiveButton("OK") { _, _ ->
                Toast.makeText(
                    context,
                    "${selectedItem.toString()} is Selected", Toast.LENGTH_LONG
                ).show();
            }
    }

    fun checkFirstChoiceTime() {
        timer(period = 10) {
            if (Date().time >= firstChoiceTime) {
                Thread {
                    UiThreadUtil.runOnUiThread(Runnable {
                        kotlin.run {
                            dialogBuilder.show()
                        }
                    })
                }.start()
                this.cancel()
            }
        }
    }
}