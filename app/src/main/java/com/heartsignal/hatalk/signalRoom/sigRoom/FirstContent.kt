package com.heartsignal.hatalk.signalRoom.sigRoom

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class FirstContent (
    val groupName: String,
    val userId: String
        )

@Parcelize
data class AnswerToQuestion(
    val currentContent: String,
    val contentsStartTime: @RawValue AnswerToQuestionTime,
    val partners: Array<Array<String>>
): Parcelable

@Parcelize
data class AnswerToQuestionTime(
    val answerToQuestion: String,
    val call: String,
    val leaveCall: String
) : Parcelable