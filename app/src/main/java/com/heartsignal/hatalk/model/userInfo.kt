package com.heartsignal.hatalk.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class userInfo(
    val kakaoUserId: String,
    val email: String,
    val photoUrl: String,
    val name: String,
    val socialNumber: String,
    val carrier: String,
    val phoneNumber: String,
    val nickname: String,
    val accessToken: String,
    val refreshToken: String,
    val gender: String,
    val age: Int
    ) : Parcelable
