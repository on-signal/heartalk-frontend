package com.example.hatalk.main.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
    var chatName: String,
    var createdAt: String,
    var updatedAt: String,
    var __v: Int
)

data class Message (val userName: String, val messageContent : String, val roomName: String, var viewType: Int)
