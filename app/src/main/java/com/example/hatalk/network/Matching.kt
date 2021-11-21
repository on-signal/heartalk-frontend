package com.example.hatalk.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


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

@Parcelize
data class roomInfo(
    val user1: userData?,
    val user2: userData?,
    val user3: userData?
) : Parcelable

@Parcelize
data class userData(
    val Id: String?,
    val nickname: String?,
    val icon: String?
) : Parcelable


@Parcelize
data class MatchingConfirmResponse(
    val msg: String?,
    var caller: String?,
    val remain_time: String?,
    val group_room_name: String?,
    val room_info: @RawValue roomInfo?,
    val question_list: Array<String>?
) : Parcelable
