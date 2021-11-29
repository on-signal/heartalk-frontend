package com.example.hatalk.main.chat

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hatalk.R
import com.example.hatalk.main.data.Message
import com.example.hatalk.main.data.Partner
import com.example.hatalk.main.data.testData
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chating.*
import org.json.JSONObject
import java.net.URISyntaxException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatingActivity : AppCompatActivity() {
    val TAG = "HEART"
    lateinit var mSocket: Socket
    lateinit var partner: Partner
    var users: Array<String> = arrayOf()


    val gson: Gson = Gson()

    //For setting the recyclerView.
    val chatList: ArrayList<Message> = arrayListOf();
    lateinit var chatingAdapter: ChatingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)

        //중요한 것은 아니며 전 액티비티에서 username을 가져왔습니다.
        //원하시는 방법 대로 username을 가져오시면 될 듯 합니다.
        var intent = intent;
        partner = intent.getParcelableExtra<Partner>("partner")!!

        chatingAdapter = ChatingAdapter(this, chatList);
        chat_recycler.adapter = chatingAdapter;

        val layoutManager = LinearLayoutManager(this)
        chat_recycler.layoutManager = layoutManager

        mSocket = ChatSocketApplication.set()
        mSocket.connect()


        val onTestConnect = Emitter.Listener { args ->
            val testJSON = JSONObject(args[0].toString())
            val temp = Gson().fromJson(testJSON.toString(), testData::class.java)
            Log.d(TAG, temp.toString())
        }

        mSocket.on("grape", onTestConnect)

        // 이제 연결이 성공적으로 되게 되면, server측에서 "connect" event 를 발생시키고
        // 아래코드가 그 event 를 핸들링 합니다. onConnect는 65번째 줄로 가서 살펴 보도록 합니다.
//        mSocket.on("newUser", onNewUser)
//        mSocket.on("myMsg", onMyMessage)
//        mSocket.on("newMsg", onNewMessage)

        //send button을 누르면 "say"라는 이벤트를 서버측으로 보낸다.
        send.setOnClickListener {
            val chat = editText.text.toString()
            editText.text.clear()
//            mSocket.emit("say", sentChat)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("h:mm a")

        return current.format(formatter)
    }


}

