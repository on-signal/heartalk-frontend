package com.heartsignal.hatalk.main.chat

import com.heartsignal.hatalk.signalRoom.PRIVATE.URLs
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketOptionBuilder
import java.net.URISyntaxException

class ChatSocketApplication {
    companion object {
        private lateinit var socket: Socket



        fun get(): Socket {
            val options = IO.Options()
            options.query = "token="
            try {
                socket = IO.socket("${URLs.URL}/contents", options)

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