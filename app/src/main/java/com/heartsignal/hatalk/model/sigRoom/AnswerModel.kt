package com.heartsignal.hatalk.model.sigRoom

import androidx.lifecycle.ViewModel
import com.heartsignal.hatalk.signalRoom.sigRoom.AnswerInfo

class AnswerModel : ViewModel() {
    private val _answerList: MutableList<AnswerInfo> = mutableListOf()
    val answerList: MutableList<AnswerInfo> get() = _answerList

    private val _ownerIdList: MutableList<String> = mutableListOf()
    val ownerIdList: List<String> get() = _ownerIdList

    fun appendAnswerList(answerInfo: AnswerInfo) {
        _answerList.add(answerInfo)
    }

    fun appendOwnerIdList(ownerId: String) {
        _ownerIdList.add(ownerId)
    }
}