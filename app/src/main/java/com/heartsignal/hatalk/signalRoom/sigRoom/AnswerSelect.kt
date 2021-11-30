package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class AnswerSelectRequest (
    val userId: String,
    val choice: String,
    val groupName: String
        )

@Parcelize
data class AnswerSelectResponse(
    val answers: Array<AnswerInfo>,
    val msg: String,
    val userId: String
) : Parcelable