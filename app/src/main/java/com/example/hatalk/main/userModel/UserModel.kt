package com.example.hatalk.main.userModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatalk.main.data.Friends
import com.example.hatalk.main.data.MatchingConfirmData
import com.example.hatalk.main.data.MatchingConfirmResponse
import com.example.hatalk.network.UserApi
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class UserModel: ViewModel() {
    init {
        Log.d("HEART", "model init")
    }

    private var _kakaoUserId: String = ""
    val kakaoUserId: String get() = _kakaoUserId

    private var _email:String = ""
    val email: String get() = _email

    private var _photoUrl:String = ""
    val photoUrl: String get() = _photoUrl

    private var _name: String = ""
    val name: String get() = _name

    private var _socialNumber: String = ""
    val socialNumber: String get() = _socialNumber

    private var _carrier: String = ""
    val carrier: String get() = _carrier

    private var _phoneNumber: String = ""
    val phoneNumber: String get() = _phoneNumber

    private var _nickname: String = ""
    val nickname: String get() = _nickname

    private var _accessToken: String = ""
    val accessToken: String get() = _accessToken

    private var _refreshToken: String = ""
    val refreshToken: String get() = _refreshToken

    private var _gender: String = ""
    val gender: String get() = _gender

    private var _age: Int = 0
    val age: Int get() = _age

    private var _friends: Array<Friends>? = null
    val friends: Array<Friends>? get() = _friends

    private fun getFriends() {
        viewModelScope.launch {
            try {
                val friendsResponse = UserApi.retrofitService.getUserFriend(_kakaoUserId)
                _friends = friendsResponse.body()
                }
            catch (e: Exception) {
            }
        }
    }

    fun setKakaoUserId(kakaoUserId: String) {
        _kakaoUserId = kakaoUserId
        getFriends()
    }


    fun setEmail(email: String) {
        _email = email
    }

    fun setProfileUrl(profileUrl: String) {
        _photoUrl = profileUrl
    }

    fun setName(name: String) {
        _name = name
    }

    fun setSocialNumber(socialNumber: String) {
        _socialNumber = socialNumber
    }

    fun setCarrier(carrier: String) {
        _carrier = carrier
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber = phoneNumber
    }

    fun setNickname(nickname: String) {
        _nickname = nickname
    }

    fun setAccessToken(accessToken: String) {
        _accessToken = accessToken
    }

    fun setRefreshToken(refreshToken: String) {
        _refreshToken = refreshToken
    }

    fun setGender(gender: String) {
        _gender = gender
    }

    fun setAge(age: Int) {
        _age = age
    }
}