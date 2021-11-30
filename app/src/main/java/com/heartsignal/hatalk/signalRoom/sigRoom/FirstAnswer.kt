package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class FirstAnswerRequest(
    val groupName: String,
    val userId: String,
    val answer: String
)

@Parcelize
data class FirstAnswerResponse(
    val answers: Array<AnswerInfo>
) : Parcelable

@Parcelize
data class AnswerInfo(
    val owner: String,
    val answer: String,
    val already: Int,
    val selector: String
) : Parcelable