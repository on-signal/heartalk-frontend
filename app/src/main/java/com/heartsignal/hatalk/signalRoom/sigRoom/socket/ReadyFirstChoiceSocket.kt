package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.exceptions.CometChatException
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import com.heartsignal.hatalk.model.sigRoom.MatchingUser
import com.heartsignal.hatalk.signalRoom.sigRoom.CallMatchingResponse
import com.heartsignal.hatalk.signalRoom.sigRoom.DirectCall
import com.heartsignal.hatalk.signalRoom.sigRoom.DirectCallActivity
import com.heartsignal.hatalk.signalRoom.sigRoom.FirstChoiceRequest
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.TimeUnit


class ReadyFirstChoiceSocket(
    private val context: Context,
    private val groupName: String,
    private val myId: String,
    private val myGender: String,
    private val myIcon: String,
    private val manList: MutableList<MatchingUser>,
    private val womanList: MutableList<MatchingUser>,
    private val TAG: String
) {
    private lateinit var socket: Socket
    private val dialogBuilder = AlertDialog.Builder(context)
    private var dialog: AlertDialog? = null
    private val womanIconList = arrayOf("여우", "햄스터", "꿀벌")
    private val manIconList = arrayOf("늑대", "펭귄", "사자")
    private lateinit var selectedItem: String

    private val onFirstChoiceConnect = Emitter.Listener { _ ->
        emitListenerForFirstChoice()
    }

    private val onFirstChoiceAnswerConnect = Emitter.Listener { args ->
        emitListenerForFirstChoiceAnswer(args)
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
        socket.on("${groupName}FirstChoice", onFirstChoiceConnect)
        socket.on("${groupName}FirstChoiceToServer", onFirstChoiceAnswerConnect)
    }

    fun disconnect() {
        socket.disconnect()
    }

    private fun emitListenerForFirstChoice() {
        if (myGender == "0") {
            choiceForMan()
        } else if (myGender == "1") {
            choiceForWoman()
        }
    }

    private fun choiceForMan() {
        dialogBuilder.setTitle("첫 인상 선택")
            .setSingleChoiceItems(womanIconList, 0) { _, pos ->
                selectedItem = womanIconList[pos]
            }.setPositiveButton("OK") { _, _ ->
                Toast.makeText(
                    context,
                    "$selectedItem is Selected", Toast.LENGTH_LONG
                ).show()
                var choice = ""
                for (woman in womanList) {
                    if (woman.icon == nameToIcon(selectedItem)) {
                        choice = woman.id
                    }
                }
                val gson = Gson()
                val firstChoice =
                    JSONObject(gson.toJson(FirstChoiceRequest(groupName, myId, myGender, choice)))
                socket.emit("firstChoiceToServer", firstChoice)
            }.setCancelable(false)


        Thread {
            UiThreadUtil.runOnUiThread(Runnable {
                kotlin.run {
                    dialog = dialogBuilder.create()
                    dialog?.show()
                }
            })
        }.start()
    }



private fun choiceForWoman() {
        dialogBuilder.setTitle("첫 인상 선택")
            .setSingleChoiceItems(manIconList, 0) { _, pos ->
                selectedItem = manIconList[pos]
            }.setPositiveButton("OK") { _, _ ->
                Toast.makeText(
                    context,
                    "$selectedItem is Selected", Toast.LENGTH_LONG
                ).show()
                var choice = ""
                for (man in manList) {
                    if (man.icon == nameToIcon(selectedItem)) {
                        choice = man.id
                    }
                }
                val gson = Gson()
                val firstChoice =
                    JSONObject(gson.toJson(FirstChoiceRequest(groupName, myId, myGender, choice)))
                socket.emit("firstChoiceToServer", firstChoice)
            }.setCancelable(false)

        Thread {
            UiThreadUtil.runOnUiThread(Runnable {
                kotlin.run {
                    dialog = dialogBuilder.create()
                    dialog?.show()
                }
            })
        }.start()
    }

    private fun emitListenerForFirstChoiceAnswer(args: Array<Any>) {
        if (dialog?.isShowing == true) {
            Thread {
                UiThreadUtil.runOnUiThread(Runnable {
                    kotlin.run {
                        dialog?.dismiss()
                    }
                })
            }.start()
        }

        val res = JSONObject(args[0].toString())
        val firstChoiceResponse = Gson().fromJson(res.toString(), CallMatchingResponse::class.java)

        var counterPartId = ""
        var counterIcon = ""
        if (myGender == "0") {
            for (partner in firstChoiceResponse.partners) {
                if (partner[0] == myId) {
                    counterPartId = partner[1]
                    break
                }
            }
            for (woman in womanList) {
                if (counterPartId == woman.id) {
                    counterIcon = woman.icon
                    break
                }
            }
        } else if (myGender == "1") {
            for (partner in firstChoiceResponse.partners) {
                if (partner[1] == myId) {
                    counterPartId = partner[0]
                    break
                }
            }
            for (man in manList) {
                if (counterPartId == man.id) {
                    counterIcon = man.icon
                    break
                }
            }
        }

        CometChat.endCall(CometChat.getActiveCall().sessionId, object : CallbackListener<Call?>() {
            override fun onSuccess(call: Call?) {
                // handle end call success
                Log.d(TAG, "CALL Ended successfully: " + call.toString())

                CometChat.removeCallListener("SignalRoomActivity")

                val intent = Intent(context, DirectCallActivity::class.java)
                val directCallObj =
                    DirectCall(myId, myGender, myIcon, counterPartId, counterIcon, groupName)


                intent.putExtra("directCallData", directCallObj)
                context.startActivity(intent)
            }

            override fun onError(e: CometChatException) {
                // handled end call error
                Log.d(TAG, "CALL Ended Error: $e")

                CometChat.removeCallListener("SignalRoomActivity")

                val intent = Intent(context, DirectCallActivity::class.java)
                val directCallObj =
                    DirectCall(myId, myGender, myIcon, counterPartId, counterIcon, groupName)


                intent.putExtra("directCallData", directCallObj)
                context.startActivity(intent)
            }
        })
    }

    private fun nameToIcon(name: String) : String {
        val myIcon = when(name) {
            "늑대" -> "wolf"
            "여우" -> "fox"
            "펭귄" -> "penguin"
            "햄스터" -> "hamster"
            "사자" -> "lion"
            else -> "bee"
        }
        return myIcon
    }

//    private fun test() {
//        val dialog = AlertDialog.Builder(context)
//            .setTitle("Notification Title")
//            .setMessage("Do you really want to delete the file?")
//            .setPositiveButton(
//                R.string.yes
//            ) { dialog, which ->
//                // TODO: Add positive button action code here
//            }
//            .setNegativeButton(R.string.no, null)
//            .create()
//
//        dialog.setOnShowListener(object : OnShowListener {
//            private val AUTO_DISMISS_MILLIS = 6000
//            override fun onShow(dialog: DialogInterface) {
//                val defaultButton: android.widget.Button? =
//                    (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
//                val negativeButtonText: CharSequence = defaultButton.getText()
//                object : CountDownTimer(AUTO_DISMISS_MILLIS.toLong(), 100) {
//                    override fun onTick(millisUntilFinished: Long) {
//                        defaultButton.setText(
//                            java.lang.String.format(
//                                Locale.getDefault(), "%s (%d)",
//                                negativeButtonText,
//                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
//                            )
//                        )
//                    }
//
//                    override fun onFinish() {
//                        if (dialog.isShowing) {
//                            dialog.dismiss()
//                        }
//                    }
//                }.start()
//            }
//        })
//        dialog.show()
//    }
}

