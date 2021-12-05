package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.exceptions.CometChatException
import com.heartsignal.hatalk.model.sigRoom.MatchingUser
import com.heartsignal.hatalk.signalRoom.sigRoom.*
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class ReadyFirstChoiceSocket(
    private val context: Context,
    private val groupName: String,
    private val myId: String,
    private val myGender: String,
    private val manList: MutableList<MatchingUser>,
    private val womanList: MutableList<MatchingUser>,
    private val TAG: String
) {
    private lateinit var socket: Socket
    private val dialogBuilder = AlertDialog.Builder(context)
    private var dialog: AlertDialog? = null
    private val womanIconList = arrayOf("fox", "hamster", "bee")
    private val manIconList = arrayOf("wolf", "penguin", "lion")
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
            .setSingleChoiceItems(womanIconList, -1) { _, pos ->
                selectedItem = womanIconList[pos]
            }.setPositiveButton("OK") { _, _ ->
                Toast.makeText(
                    context,
                    "$selectedItem is Selected", Toast.LENGTH_LONG
                ).show()
                var choice = ""
                for (woman in womanList) {
                    if (woman.icon == selectedItem) {
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
            .setSingleChoiceItems(manIconList, -1) { _, pos ->
                selectedItem = manIconList[pos]
            }.setPositiveButton("OK") { _, _ ->
                Toast.makeText(
                    context,
                    "$selectedItem is Selected", Toast.LENGTH_LONG
                ).show()
                var choice = ""
                for (man in manList) {
                    if (man.icon == selectedItem) {
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
                    DirectCall(myId, myGender, counterPartId, counterIcon, groupName)


                intent.putExtra("directCallData", directCallObj)
                context.startActivity(intent)
            }

            override fun onError(e: CometChatException) {
                // handled end call error
                Log.d(TAG, "CALL Ended Error: $e")

                CometChat.removeCallListener("SignalRoomActivity")

                val intent = Intent(context, DirectCallActivity::class.java)
                val directCallObj =
                    DirectCall(myId, myGender, counterPartId, counterIcon, groupName)


                intent.putExtra("directCallData", directCallObj)
                context.startActivity(intent)
            }
        })
    }
}