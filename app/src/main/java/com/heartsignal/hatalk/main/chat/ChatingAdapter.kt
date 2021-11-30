package com.heartsignal.hatalk.main.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heartsignal.hatalk.GlobalApplication
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.main.data.ChatMessage

class ChatingAdapter(val context: Context, val chatList: MutableList<ChatMessage>) : RecyclerView.Adapter<ChatingAdapter.ViewHolder>() {

    val CHAT_MINE = 0
    val CHAT_PARTNER = 1
    val USER_JOIN = 2
    val USER_LEAVE = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view : View? = null
        when(viewType){

            0 ->
            {
                view = LayoutInflater.from(context).inflate(R.layout.item_chating_user,parent,false)
                Log.d("user inflating","viewType : ${viewType}")
            }

            1 ->
            {
                view = LayoutInflater.from(context).inflate(R.layout.item_chating_partner,parent,false)
                Log.d("partner inflating","viewType : ${viewType}")
            }
        }

        return ViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return chatList?.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].senderId == GlobalApplication.userInfo.kakaoUserId) 0 else 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageData  = chatList[position]
        val userName = messageData.senderId
        val content = messageData.text
        val viewType = getItemViewType(position)

        when(viewType) {

            CHAT_MINE -> {
                holder.message.setText(content)
            }
            CHAT_PARTNER ->{
                holder.userName.setText(userName)
                holder.message.setText(content)
            }
        }

    }
    inner class ViewHolder(itemView : View):  RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.username)
        val message = itemView.findViewById<TextView>(R.id.message)
        val text = itemView.findViewById<TextView>(R.id.text)
    }
}