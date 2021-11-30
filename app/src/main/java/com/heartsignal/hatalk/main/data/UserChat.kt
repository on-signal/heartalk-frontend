package com.heartsignal.hatalk.main.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class Friends (
    var _id : String,
    var chatName : String,
    var ownerId : String,
    var partner : Partner,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int,
    var recentMessage: recentMessage?,
    var id: String
)

@Parcelize
data class Partner (
    var id : String,
    var nickname : String,
    var photoUrl : String
) : Parcelable


data class recentMessage (
    var _id: String,
    var text: String,
    var senderId: String,
    var chatName: String
)
@Parcelize
data class ChatData (
    var _id : String,
    var chatName : String,
    var ownerId : String,
    var partner : Partner,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int,
    var messages: MutableList<ChatMessage>?,
    var id: String
) : Parcelable


@Parcelize
data class ChatMessage (
    var _id : String,
    var text: String,
    var senderId: String,
    var chatName: String
) : Parcelable

data class Message (val userName: String, val messageContent : String, val roomName: String, var viewType: Int)
