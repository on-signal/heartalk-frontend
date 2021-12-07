package com.heartsignal.hatalk.signalRoom.sigRoom

data class FirstChoiceRequest(
    val groupName: String,
    val userId: String,
    val gender: String,
    val choice: String
)
