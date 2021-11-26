package com.example.hatalk.model.sigRoom

import androidx.lifecycle.ViewModel

data class MatchingUser(
    private val id: String,
    private val nickname: String,
    private val gender: String,
    public val icon: String
)

class MatchingModel : ViewModel() {
    private var _groupRoomName: String = ""
    val groupRoomName: String get() = _groupRoomName

    private var _caller: String = ""
    val caller: String get() = _caller

    private var _myId: String = ""
    val myId: String get() = _myId

    private var _myNickname: String = ""
    val myNickname: String get() = _myNickname

    private var _myIcon: String = ""
    val myIcon: String get() = _myIcon

    private var _myGender: String = "0"
    val myGender: String get() = _myGender

    private val _manList: MutableList<MatchingUser> = mutableListOf()
    val manList: MutableList<MatchingUser> get() = _manList

    private val _womanList: MutableList<MatchingUser> = mutableListOf()
    val womanList: MutableList<MatchingUser> get() = _womanList

    private val _questionList: MutableList<String> = mutableListOf()
    val questionList: MutableList<String> get() = _questionList


    fun setGroupRoomName(groupRoomName: String) {
        _groupRoomName = groupRoomName
    }

    fun setCaller(caller: String) {
        _caller = caller
    }

    fun setMyId(myId: String) {
        _myId = myId
    }

    fun setMyNickname(myNickname: String) {
        _myNickname = myNickname
    }

    fun setMyIcon(myIcon: String) {
        _myIcon = myIcon
    }

    fun setMyGender(myGender: String) {
        _myGender = myGender
    }

    fun appendManList(man: MatchingUser) {
        _manList.add(man)
    }

    fun appendWomanList(woman: MatchingUser) {
        _womanList.add(woman)
    }

    fun appendQuestionList(question: String) {
        _questionList.add(question)
    }

}