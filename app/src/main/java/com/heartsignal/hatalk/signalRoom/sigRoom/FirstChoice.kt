package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class FirstChoiceRequest(
    val groupName: String,
    val userId: String,
    val gender: String,
    val choice: String
)

@Parcelize
data class FirstChoiceResponse(
    val partners: Array<Array<String>>
) : Parcelable