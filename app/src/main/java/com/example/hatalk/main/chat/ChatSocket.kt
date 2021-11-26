package com.example.hatalk.main.chat

import com.example.hatalk.signalRoom.PRIVATE.URLs
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class ChatSocketApplication {
    companion object SocketHandler {

        lateinit var mSocket: Socket

        @Synchronized
        fun setSocket() {
            try {
                mSocket = IO.socket("${URLs.URL}/room")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }

        @Synchronized
        fun getSocket(): Socket {
            return mSocket
        }

        @Synchronized
        fun establishConnection() {
            mSocket.connect()
        }

        @Synchronized
        fun closeConnection() {
            mSocket.disconnect()
        }
    }
}