package com.heartsignal.hatalk.main.chat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.heartsignal.hatalk.R
import com.google.gson.Gson
import com.heartsignal.hatalk.GlobalApplication
import com.heartsignal.hatalk.main.data.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chating.*
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatingActivity : AppCompatActivity() {
    val TAG = "HEART"
    lateinit var mSocket: Socket
    lateinit var partner: Partner
    lateinit var chatData: ChatData
    var users: Array<String> = arrayOf()


    val gson: Gson = Gson()

    //For setting the recyclerView.
    var chatList: MutableList<ChatMessage>? = null
    lateinit var chatingAdapter: ChatingAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)
        Log.d(TAG, LocalDateTime.now().toString())
        Log.d(TAG, setTime())


        var intent = intent;
        partner = intent.getParcelableExtra<Partner>("partner")!!
        GlobalApplication.tempPartner = partner
        chatData = intent.getParcelableExtra<ChatData>("chatMessage")!!
        chatList = chatData.messages

        if (chatList != null) {
            chatingAdapter = ChatingAdapter(this, chatList!!)
            chat_recycler.adapter = chatingAdapter;
        }

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


        send.setOnClickListener {
            val chat = editText.text.toString()
            val tempMessage = ChatMessage(
                "",
                chat,
                GlobalApplication.userInfo.kakaoUserId,
                chatData.name,
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString(),
                0
            )
            addItemToRecyclerView(tempMessage)
            editText.text.clear()

//            mSocket.emit("say", sentChat)
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
        val formatter = DateTimeFormatter.ofPattern("h:mm")

        return current.format(formatter)
    }

    private fun addItemToRecyclerView(chatMessage: ChatMessage) {

        //Since this function is inside of the listener,
        // You need to do it on UIThread!
        runOnUiThread {
            chatList?.add(chatMessage)
            Log.d(TAG, chatList!!.size.toString())
            chatingAdapter.notifyItemInserted(chatList!!.size)
            chat_recycler.scrollToPosition(chatList!!.size - 1) //move focus on last message
        }
    }

}

