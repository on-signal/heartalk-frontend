package com.example.hatalk.main

import com.example.hatalk.signalRoom.PRIVATE.URLs
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class MatchingSoketApplication {
    companion object {
        private lateinit var socket: Socket
        fun get(): Socket {
            try {
                socket = IO.socket("${URLs.URL}/room")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return socket
        }
    }
}