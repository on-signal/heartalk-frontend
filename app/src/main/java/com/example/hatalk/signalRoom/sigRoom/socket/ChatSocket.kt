package com.example.hatalk.signalRoom.sigRoom.socket

import android.view.View
import android.widget.TextView
import com.example.hatalk.signalRoom.sigRoom.TempMessage
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_signal_room.view.*
import org.json.JSONObject
import java.net.URISyntaxException

class ChatSocket(
    private val view: View,
    private val groupRoomName: String,
    private val myId: String,
    private val myIcon: String
) {
    private lateinit var socket: Socket
    private val onChatConnect = Emitter.Listener { args ->
        emitListener(args)
    }

    fun set() {
        try {
            socket = ContentsSocketApplication.get()
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace();
        }

        setOnClick()
    }

    fun makeOn() {
        socket.on(groupRoomName, onChatConnect)
    }

    fun disconnect() {
        socket.disconnect()
    }

    private fun setOnClick() {
        val chatButton = view.button_chat_send

        chatButton.setOnClickListener {
            val chatText = view.edit_chat_message
            val message = TempMessage(
                groupRoomName,
                myId,
                chatText.text.toString(),
                myIcon
            )
            val gson = Gson()
            val obj = JSONObject(gson.toJson(message))

            socket.emit("msgToServer", obj)
            chatText.text.clear()
        }
    }

    private fun emitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())

        var userChat: TextView? = null
        when (res.getString("icon")) {
            "lion" -> {
                userChat = view.lion_text
            }
            "bee" -> {
                userChat = view.bee_text
            }
            "penguin" -> {
                userChat = view.penguin_text
            }
            "hamster" -> {
                userChat = view.hamster_text
            }
            "wolf" -> {
                userChat = view.wolf_text
            }
            "fox" -> {
                userChat = view.fox_text
            }
        }

        Thread {
            runOnUiThread(Runnable {
                kotlin.run {
                    userChat?.text = res.getString("text")
                }
            })
        }.start()
    }
}