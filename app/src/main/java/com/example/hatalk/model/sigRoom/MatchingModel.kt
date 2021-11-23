package com.example.hatalk.model.sigRoom

import androidx.lifecycle.ViewModel

class MatchingModel: ViewModel() {
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

    private var _user1Id: String = ""
    val user1Id: String get() = _user1Id

    private var _user1Nickname: String = ""
    val user1Nickname: String get() = _user1Nickname

    private var _user1Icon: String = ""
    val user1Icon: String get() = _user1Icon

    private var _user2Id: String = ""
    val user2Id: String get() = _user2Id

    private var _user2Nickname: String = ""
    val user2Nickname: String get() = _user2Nickname

    private var _user2Icon: String = ""
    val user2Icon: String get() = _user2Icon

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

    fun setUser1Id(user1Id: String) {
        _user1Id = user1Id
    }

    fun setUser1Nickname(user1Nickname: String) {
        _user1Nickname = user1Nickname
    }

    fun setUser1Icon(user1Icon: String) {
        _user1Icon = user1Icon
    }

    fun setUser2Id(user2Id: String) {
        _user2Id = user2Id
    }

    fun setUser2Nickname(user2Nickname: String) {
        _user2Nickname = user2Nickname
    }

    fun setUser2Icon(user2Icon: String) {
        _user2Icon = user2Icon
    }
}