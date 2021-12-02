package com.heartsignal.hatalk.main.chat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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


    val gson: Gson = Gson()

    //For setting the recyclerView.
    var chatList: MutableList<ChatMessage>? = null
    lateinit var chatingAdapter: ChatingAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)


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

        chat_recycler.scrollToPosition(chatList!!.size - 1)
        val partnerNameView: TextView = findViewById<TextView>(R.id.partner_name)
        partnerNameView.text = partner.nickname

        mSocket = ChatSocketApplication.set()
        mSocket.connect()

        enterRoom(chatData.name)


        val onListenConnect = Emitter.Listener { args ->
            Log.d(TAG, "Hello onListen")
            val messageJSON = JSONObject(args[0].toString())
            val listenMessage = Gson().fromJson(messageJSON.toString(), ListenData::class.java)
            Log.d(TAG, listenMessage.toString())
            val tempMessage = ChatMessage(
                "",
                listenMessage.text,
                listenMessage.sendTime,
                listenMessage.senderKakaoUserId,
                listenMessage.chatName,
                listenMessage.sendTime,
                listenMessage.sendTime,
                0
            )
            addItemToRecyclerView(tempMessage)
        }

        mSocket.on("messageAdded", onListenConnect)


        send.setOnClickListener {
            val chat = edit_text.text.toString()
            val tempTime = setTime()
            Log.d(TAG, tempTime)
            val tempMessage = ChatMessage(
                "",
                chat,
                tempTime,
                GlobalApplication.userInfo.kakaoUserId,
                chatData.name,
                tempTime,
                tempTime,
                0
            )
            emitMessage(chat, chatData.name, tempTime)
            addItemToRecyclerView(tempMessage)
            edit_text.text.clear()
        }

    }

    private fun emitMessage(chatText: String, roomName: String, time: String) {
        val emitMessage = EmitData(chatText, roomName, time)
        val gson = Gson()
        val emitMessageObj = JSONObject(gson.toJson(emitMessage))

        mSocket.emit("addMessage", emitMessageObj)
    }


    private fun enterRoom(roomName: String) {
        val enterMessage = roomName

        Log.d(TAG, "enter room")
        mSocket.emit("joinChat", enterMessage)
    }

    private fun leaveRoom() {
//        val enterMessage = roomName

        Log.d(TAG, "leave room")
        mSocket.emit("leaveChat")
    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveRoom()
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
            chatingAdapter.notifyItemInserted(chatList!!.size)
            chat_recycler.scrollToPosition(chatList!!.size - 1) //move focus on last message
        }
    }

}

