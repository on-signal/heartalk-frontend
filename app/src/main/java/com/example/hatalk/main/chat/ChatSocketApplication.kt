package com.example.hatalk.main.chat

import com.example.hatalk.signalRoom.PRIVATE.URLs
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class ChatSocketApplication {
    companion object {
        private lateinit var socket: Socket
        fun get(): Socket {
            try {
                socket = IO.socket("${URLs.URL}/contents")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return socket
        }

        fun set(): Socket {
            return socket
        }
    }
}