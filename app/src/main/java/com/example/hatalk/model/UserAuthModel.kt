package com.example.hatalk.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatalk.network.LoginRequest
import com.example.hatalk.network.UserApi
import kotlinx.coroutines.launch
import java.lang.Exception

class UserAuthModel: ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _photoUrl = MutableLiveData<String>()
    val photoUrl: LiveData<String> get() = _photoUrl

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> get() = _accessToken

    private val _refreshToken = MutableLiveData<String>()
    val refreshToken: LiveData<String> get() = _refreshToken

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    fun setEmail(email: String) {
        if (_email.value != null) {
            return
        } else {
            _email.value = email
        }
    }

    fun setProfileUrl(profileUrl: String) {
        if(_photoUrl.value != null) {
            return
        } else {
            _photoUrl.value = profileUrl
        }
    }

     fun login(body: LoginRequest) {
        viewModelScope.launch {
            try {
                val response = UserApi.retrofitService.login(body)
                Log.d("TEST: ", response.toString())
                _loginSuccess.value = response.code() != 404

//                _accessToken.value = user.body()?.accessToken
//                _refreshToken.value = user.body()?.refreshToken
            } catch (e: Exception) {
                Log.d("TEST: ", e.toString())
                _loginSuccess.value = false
            }
        }
    }
}