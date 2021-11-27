package com.example.hatalk.signalRoom.sigRoom.socket

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.hatalk.model.sigRoom.MatchingUser
import com.example.hatalk.signalRoom.sigRoom.FirstChoice
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
    private val womanList: MutableList<MatchingUser>
) {
    private lateinit var socket: Socket
    private val dialogBuilder = AlertDialog.Builder(context)
    private val womanIconList = arrayOf("fox")
    private val manIconList = arrayOf("wolf")
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
                    JSONObject(gson.toJson(FirstChoice(groupName, myId, myGender, choice)))
                socket.emit("firstChoiceToServer", firstChoice)
            }
        Thread {
            UiThreadUtil.runOnUiThread(Runnable {
                kotlin.run {
                    dialogBuilder.show()
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
                    JSONObject(gson.toJson(FirstChoice(groupName, myId, myGender, choice)))
                socket.emit("firstChoiceToServer", firstChoice)
            }
        Thread {
            UiThreadUtil.runOnUiThread(Runnable {
                kotlin.run {
                    dialogBuilder.show()
                }
            })
        }.start()
    }

    private fun emitListenerForFirstChoiceAnswer(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
        Log.d("Res: ", res.toString())
    }
}