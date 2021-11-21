package com.example.hatalk.network


data class MatchingRequest(
    val userId: String,
    val nickname: String
)

data class MatchingResponse(
    val success: Boolean?
)

data class MatchingConfirmRequest(
    val userId: String
)

data class roomInfo(
    val user1: userData?,
    val user2: userData?,
    val user3: userData?
)

data class userData(
    val Id: String?,
    val nickname: String?,
    val icon: String?
)



data class MatchingConfirmResponse(
    val msg: String?,
    val caller: String?,
    val remain_time: String?,
    val group_room_name: String?,
    val room_info: roomInfo?,
    val question_list: Array<String>?
)
