package com.example.hatalk.main.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hatalk.R
import com.example.hatalk.main.ChatService
import com.example.hatalk.main.data.MatchingSocketApplication
import com.example.hatalk.main.data.Message
import com.example.hatalk.main.data.Partner
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chating.*
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class ChatingActivity : AppCompatActivity() {
    val TAG = "HEART"
    lateinit var mSocket: Socket
    lateinit var partner: Partner
    var users: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)
        try {
            //IO.socket 메소드는 은 저 URL 을 토대로 클라이언트 객체를 Return 합니다.
            mSocket = ChatSocketApplication.get()
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("MainActivity", e.reason)
        }

        //중요한 것은 아니며 전 액티비티에서 username을 가져왔습니다.
        //원하시는 방법 대로 username을 가져오시면 될 듯 합니다.
        var intent = intent;
        partner = intent.getParcelableExtra<Partner>("partner")!!
        Log.d(TAG, partner.toString())


        // 이제 연결이 성공적으로 되게 되면, server측에서 "connect" event 를 발생시키고
        // 아래코드가 그 event 를 핸들링 합니다. onConnect는 65번째 줄로 가서 살펴 보도록 합니다.
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("newUser", onNewUser)
        mSocket.on("myMsg", onMyMessage)
        mSocket.on("newMsg", onNewMessage)

        //send button을 누르면 "say"라는 이벤트를 서버측으로 보낸다.
        send.setOnClickListener {
            val chat = editText.text.toString()
            editText.text.clear()
            mSocket.emit("say", chat)
        }



    }

    // onConnect는 Emmiter.Listener를 구현한 인터페이스입니다.
    // 여기서 Server에서 주는 데이터를 어떤식으로 처리할지 정하는 거죠.
    val onConnect: Emitter.Listener = Emitter.Listener {
        //여기서 다시 "login" 이벤트를 서버쪽으로 username 과 함께 보냅니다.
        //서버 측에서는 이 username을 whoIsON Array 에 추가를 할 것입니다.
        mSocket.emit("login", partner.nickname)
        Log.d(TAG, "Socket is connected with ${partner.nickname}")
    }


    val onMyMessage = Emitter.Listener {
        Log.d("on", "Mymessage has been triggered.")
        Log.d("mymsg : ", it[0].toString())
    }

    val onNewMessage = Emitter.Listener {
        Log.d("on", "New message has been triggered.")
        Log.d("new msg : ", it[0].toString())
    }


    val onMessageRecieved: Emitter.Listener = Emitter.Listener {
        try {
            val receivedData: Any = it[0]
            Log.d(TAG, receivedData.toString())

        } catch (e: Exception) {
            Log.e(TAG, "error", e)
        }
    }

    val onNewUser: Emitter.Listener = Emitter.Listener {

        var data = it[0] //String으로 넘어옵니다. JSONArray로 넘어오지 않도록 서버에서 코딩한 것 기억나시죠?
        if (data is String) {
            users = data.split(",").toTypedArray() //파싱해줍니다.
            for (a: String in users) {
                Log.d("user", a) //누구누구 있는지 한 번 쫘악 뽑아줘봅시다.
            }
        } else {
            Log.d("error", "Something went wrong")
        }

    }

    override fun onStop() {
        super.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
    }




}

