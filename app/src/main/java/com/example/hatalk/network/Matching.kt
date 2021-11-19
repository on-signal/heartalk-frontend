package com.example.hatalk.network

import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class MatchingRequest(
    val userId: String,
    val nickname: String
)

data class MatchingResponse(
    val success: Boolean
)

data class MatchingConfirmRequest(
    val userId: String
)

data class room_info(
    val new1: Array<String>,
    val new2: Array<String>
)


data class MatchingConfirmResponse(
    val msg: String?,
    val caller: String?,
    val remain_time: String?,
    val group_room_name: String?,
    val room_info: room_info
)
