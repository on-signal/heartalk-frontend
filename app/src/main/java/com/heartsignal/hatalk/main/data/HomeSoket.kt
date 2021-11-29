package com.heartsignal.hatalk.main.data

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
    val groupName: String,
    val successMsg: Boolean
)

@Parcelize
data class MatchingConfirmResponse(
    val msg: String?,
    var caller: String?,
    val remain_time: String?,
    val groupName: String,
    val gender: String?,
    val room_info: @RawValue roomInfo?,
    val question_list: Array<String>?
) : Parcelable

@Parcelize
data class roomInfo(
    val user1: userData?,
    val user2: userData?,
    val user3: userData?,
    val user4: userData?
) : Parcelable

@Parcelize
data class userData(
    val Id: String?,
    val nickname: String?,
    val gender: String?,
    val icon: String?
) : Parcelable


data class testData(
    val name: String,
    val groupName: String,
    val text: String,
    val icon: String
)