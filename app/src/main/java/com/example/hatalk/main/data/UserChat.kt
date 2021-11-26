package com.example.hatalk.main.data

data class Friends (
    var _id : String,
    var chatName : String,
    var ownerId : String,
    var partner : Partner,
    var createdAt : String,
    var updatedAt : String,
    var __v : String,
    var id: String
)


data class Partner (
    var id : String,
    var nickname : String,
    var photoUrl : String
)

data class Message (val userName: String, val messageContent : String, val roomName: String, var viewType: Int)
