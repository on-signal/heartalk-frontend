package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.content.Context
import android.content.Intent
import android.util.Log
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.heartsignal.hatalk.signalRoom.sigRoom.OneToOneCallActivity
import com.heartsignal.hatalk.signalRoom.sigRoom.OnetoOneCall
import com.heartsignal.hatalk.signalRoom.sigRoom.SignalRoomActivity
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException

class FirstCallEndSocket(
    private val context: Context,
    private val groupName: String,
    private val TAG: String
) {
    private lateinit var socket: Socket
    private val onFirstCallEnd = Emitter.Listener {
        emitListener()
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
        socket.on("${groupName}FirstCallEnd", onFirstCallEnd)
    }

    private fun emitListener() {
        CometChat.endCall(
            CometChat.getActiveCall().sessionId,
            object : CometChat.CallbackListener<Call?>() {
                override fun onSuccess(call: Call?) {
                    // handle end call success
                    Log.d(TAG, "CALL Ended successfully: " + call.toString())

                    val intent = Intent(context, SignalRoomActivity::class.java)
                    context.startActivity(intent)
                }

                override fun onError(e: CometChatException) {
                    // handled end call error
                    Log.d(TAG, "CALL Ended Error: $e")

                    val intent = Intent(context, SignalRoomActivity::class.java)
                    context.startActivity(intent)
                }
            })
    }
}