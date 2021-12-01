package com.heartsignal.hatalk.main.chat

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heartsignal.hatalk.GlobalApplication
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.main.data.ChatMessage
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
        return if (chatList[position].senderKakaoUserId == GlobalApplication.userInfo.kakaoUserId) 0 else 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageData  = chatList[position]
        val nickName = GlobalApplication.tempPartner?.nickname
        val content = messageData.text

        val currentTime = messageData.sendTime


        val viewType = getItemViewType(position)

        when(viewType) {

            CHAT_MINE -> {
                holder.message.text = content
            }
            CHAT_PARTNER ->{
                holder.userName.text = nickName.toString()
                holder.message.text = content
                holder.time.text = currentTime
                Glide.with(holder.itemView.context).load(GlobalApplication.tempPartner?.photoUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.profile)

            }
        }

    }
    inner class ViewHolder(itemView : View):  RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.username)
        val message = itemView.findViewById<TextView>(R.id.message)
        val profile = itemView.findViewById<ImageView>(R.id.messageItem_imageview_profile)
        val time = itemView.findViewById<TextView>(R.id.message_time)
    }
}