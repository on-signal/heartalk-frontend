package com.example.hatalk.model

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatalk.SignUpActivity
import com.example.hatalk.network.LoginRequest
import com.example.hatalk.network.UserApi
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class UserAuthModel: ViewModel() {
    private var _email:String? = null
    val email: String? get() = _email

    private var _name: String? = null
    val name: String? get() = _name

    private var _photoUrl:String? = null
    val photoUrl: String? get() = _photoUrl

    private var _accessToken: String? = null
    val accessToken: String? get() = _accessToken

    private var _refreshToken: String? = null
    val refreshToken: String? get() = _refreshToken

    private var _loginSuccess: Boolean? = null

    fun getLoginSucess(): Boolean? {
        return _loginSuccess
    }

    fun setEmail(email: String) {
        if (_email != null) {
            return
        } else {
            _email = email
        }
    }

    fun setProfileUrl(profileUrl: String) {
        if(_photoUrl != null) {
            return
        } else {
            _photoUrl = profileUrl
        }
    }

    fun setNickname(nickname: String) {
        if(_name != null) {
            return
        } else {
            _name = nickname
        }
    }

     fun login(body: LoginRequest): Job {
        return viewModelScope.launch(Dispatchers.IO)  {
            try {
                val response = UserApi.retrofitService.login(body)
                _loginSuccess = response.code().toString() != "401"
                _accessToken = response.body()?.accessToken
            } catch (e: Exception) {
                _loginSuccess = false
            }
        }
    }
}