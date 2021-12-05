package com.heartsignal.hatalk.signalRoom.sigRoom.dataFormat

import android.os.Parcelable
import com.heartsignal.hatalk.main.data.Partner
import com.heartsignal.hatalk.main.data.recentMessage
import kotlinx.android.parcel.Parcelize

data class KeepTalking(
    val groupName: String,
    val userId: String,
    val choice: String
)

@Parcelize
data class TalkingInfo (
    val _id : String,
    val name : String,
    val ownerKakaoUserId : String,
    val partner : Partner,
    val createdAt : String,
    val updatedAt : String,
    val __v : Int,
    val id: String
) : Parcelable

@Parcelize
data class KeepTalkingResult(
    val success: Boolean,
    val info: TalkingInfo?
) : Parcelable