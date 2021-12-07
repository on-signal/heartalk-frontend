package com.heartsignal.hatalk.main.chat

import android.util.Log
import com.heartsignal.hatalk.signalRoom.PRIVATE.URLs
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketOptionBuilder
import retrofit2.http.Header
import java.net.URISyntaxException
import io.socket.emitter.Emitter
import java.util.*


class ChatSocketApplication {
    companion object {
        private lateinit var socket: Socket

        fun get(token: String): Socket {
            val options: IO.Options = IO.Options()

            val authorization : Map<String, String> = mapOf("Authorization" to token)

            options.auth = authorization
//            options.extraHeaders["Authorization"] = Arrays.asList(token)

//            Log.d("HEART", options.extraHeaders.)
            try {
                socket = IO.socket("${URLs.URL}/", options)
                Log.d("HEART", "get matching SOCKET")

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