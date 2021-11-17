package com.example.hatalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.hatalk.network.LoginRequest
import com.example.hatalk.network.UserApi
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.*

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        UserApiClient.instance.me { user, error ->
            val loginRequest = LoginRequest(user?.kakaoAccount?.email!!)
            val signUpIntent = Intent(this, SignUpActivity::class.java)
            val myPageIntent = Intent(this, SecondActivity::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                val response =
                    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                        UserApi.retrofitService.login(loginRequest)
                    }
                val code = response.code().toString()
                if (code == "401") {
                    signUpIntent.putExtra("email", user.kakaoAccount?.email)
                    signUpIntent.putExtra("photoUrl", user.kakaoAccount?.profile?.profileImageUrl)

                    startActivity(signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                } else {
                    startActivity(myPageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }
    }
}
