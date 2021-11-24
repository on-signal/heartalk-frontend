package com.example.hatalk.main.data

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class MatchingStartData(
    val userId: String,
    val nickname: String,
    val gender: String
)

data class MatchingConfirmData(
    val userId: String,
    val group_room_name: String
)

@Parcelize
data class MatchingConfirmResponse(
    val msg: String?,
    var caller: String?,
    val remain_time: String?,
    val group_room_name: String?,
    val gender: String?,
    val room_info: @RawValue roomInfo?,
    val question_list: Array<String>?
) : Parcelable

@Parcelize
data class roomInfo(
    val user1: userData?,
    val user2: userData?
) : Parcelable

@Parcelize
data class userData(
    val Id: String?,
    val nickname: String?,
    val gender: String?,
    val icon: String?
) : Parcelable