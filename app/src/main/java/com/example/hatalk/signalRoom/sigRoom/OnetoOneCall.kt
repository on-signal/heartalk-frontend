package com.example.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OnetoOneCall(
    val myId: String,
    val counterPartId: String,
    val myGender: String
) : Parcelable

