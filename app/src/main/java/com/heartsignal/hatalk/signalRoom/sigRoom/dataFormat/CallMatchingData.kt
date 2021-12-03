package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CallMatchingResponse(
    val partners: Array<Array<String>>
) : Parcelable