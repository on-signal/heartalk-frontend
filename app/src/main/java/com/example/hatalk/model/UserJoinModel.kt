package com.example.hatalk.model

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatalk.SignUpActivity
import com.example.hatalk.network.LoginRequest
import com.example.hatalk.network.SignUpRequest
import com.example.hatalk.network.UserApi
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class UserJoinModel: ViewModel() {
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

    private var _loginSuccess: Boolean = false

    fun getLoginSucess(): Boolean? {
        return _loginSuccess
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
}