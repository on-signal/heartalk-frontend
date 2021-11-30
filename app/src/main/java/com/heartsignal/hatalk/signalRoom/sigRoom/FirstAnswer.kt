package com.heartsignal.hatalk.signalRoom.sigRoom

import kotlinx.android.parcel.Parcelize

data class FirstAnswerRequest (
    val groupName: String,
    val userId: String,
    val answer: String
        )

//@Parcelize
//data class FirstAnswerResponse(
//
//)