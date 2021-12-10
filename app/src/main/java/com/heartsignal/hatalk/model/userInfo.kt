package com.heartsignal.hatalk.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class userInfo(
    val kakaoUserId: String,
    var email: String?,
    val photoUrl: String,
    val name: String,
    val socialNumber: String,
    val carrier: String,
    var phoneNumber: String,
    var nickname: String,
    val accessToken: String,
    val refreshToken: String,
    val gender: String,
    val age: Int
    ) : Parcelable
