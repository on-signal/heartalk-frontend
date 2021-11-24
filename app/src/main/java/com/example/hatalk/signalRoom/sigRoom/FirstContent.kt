package com.example.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

data class FirstContent (
    val groupName: String,
    val userId: String
        )

@Parcelize
data class IntroContentsResponse(
    val currentContent: String,
    val contentsStartTime: @RawValue IntroContentsStartTime
) : Parcelable

@Parcelize
data class IntroContentsStartTime(
    val introduction: String,
    val firstChoice: String
) : Parcelable
