package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DirectCall(
    val myId: String,
    val myGender: String,
    val counterPartId: String,
    val counterPartIcon: String,
    val groupName: String,
) : Parcelable

@Parcelize
data class DirectCallAvailable(
    val groupName: String,
    val contents: String
) : Parcelable