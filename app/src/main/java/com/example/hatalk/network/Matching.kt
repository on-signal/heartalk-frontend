package com.example.hatalk.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


data class MatchingRequest(
    val userId: String,
    val nickname: String,
    val gender: String
)

data class MatchingResponse(
    val success: Boolean?
)

data class DeleteRoomRequest(
    val guid: String
)

data class DeleteRoomResponse(
    val msg: String
)
