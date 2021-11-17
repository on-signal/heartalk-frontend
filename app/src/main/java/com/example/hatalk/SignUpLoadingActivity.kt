package com.example.hatalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.hatalk.network.SignUpRequest
import com.example.hatalk.network.UserApi
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.*

class SignUpLoadingActivity : AppCompatActivity() {
    init {
        val email = intent?.extras?.getString("email").toString()
        val name = intent?.extras?.getString("name").toString()
        val socialNumber = intent?.extras?.getString("socialNumber").toString()
        val carrier = intent?.extras?.getString("carrier").toString()
        val phoneNumber = intent?.extras?.getString("phoneNumber").toString()
        val nickname = intent?.extras?.getString("nickname").toString()
        val photoUrl = intent?.extras?.getString("photoUrl").toString()

        val signUpRequest =
            SignUpRequest(email, name, socialNumber, carrier, phoneNumber, nickname, photoUrl)

        Log.d("Name: ", name)
        val secondActivityIntent = Intent(this, SecondActivity::class.java)
        lifecycleScope.launch {
            val response = UserApi.retrofitService.signUp(signUpRequest)
            Log.d("SignUp1: ", response.toString())
            startActivity(secondActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_loading)
    }
}