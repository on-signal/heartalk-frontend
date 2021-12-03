package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.app.Activity
import android.content.Context
import android.util.Log
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException

class CallEndSocket(
    private val context: Context,
    private val groupName: String,
    private val TAG: String
) {
    private lateinit var socket: Socket
    private val onCallEnd = Emitter.Listener {
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
        socket.on("${groupName}FirstCallEnd", onCallEnd)
        socket.on("${groupName}SecondCallEnd", onCallEnd)
    }

    private fun emitListener() {
        CometChat.endCall(
            CometChat.getActiveCall().sessionId,
            object : CometChat.CallbackListener<Call?>() {
                override fun onSuccess(call: Call?) {
                    // handle end call success
                    Log.d(TAG, "CALL Ended successfully: " + call.toString())

                    CometChat.removeCallListener("OneToOneCallActivity")

                    val activity = context as Activity
                    activity.finish()
                }

                override fun onError(e: CometChatException) {
                    // handled end call error
                    Log.d(TAG, "CALL Ended Error: $e")

                    CometChat.removeCallListener("OneToOneCallActivity")

                    val activity = context as Activity
                    activity.finish()
                }
            })
    }

    fun disconnect() {
        socket.disconnect()
    }
}