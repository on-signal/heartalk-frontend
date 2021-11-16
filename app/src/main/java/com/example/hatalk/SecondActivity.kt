package com.example.hatalk

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.kakao.sdk.user.UserApiClient
import androidx.fragment.app.activityViewModels
import com.example.hatalk.model.UserAuthModel
import com.example.hatalk.network.LoginRequest
import kotlinx.coroutines.runBlocking

class SecondActivity : AppCompatActivity() {
    private val userAuthModel: UserAuthModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val email = findViewById<TextView>(R.id.kakao_email)
        val nickname = findViewById<TextView>(R.id.nickname)
        val profileImage = findViewById<TextView>(R.id.profile_image_url)

        UserApiClient.instance.me { user, error ->
            email.text = "이메일: ${user?.kakaoAccount?.email}"
            nickname.text = "닉네임: ${user?.kakaoAccount?.profile?.nickname}"
            profileImage.text = "URL: ${user?.kakaoAccount?.profile?.profileImageUrl}"
            userAuthModel.setEmail(user?.kakaoAccount?.email.toString())
            userAuthModel.setProfileUrl(user?.kakaoAccount?.profile?.profileImageUrl.toString())
            val loginRequest = LoginRequest(user?.kakaoAccount?.email!!)

            runBlocking {
                val loginJob = userAuthModel.login(loginRequest)
                loginJob.join()
            }

            if (userAuthModel.getLoginSucess() == false) {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        // 임시로 회원가입으로 이동하는 버튼

        val tempToSignupButton = findViewById<Button>(R.id.temp_to_signup_button)

        tempToSignupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
        }

        val kakaoLoginButton =  findViewById<Button>(R.id.kakao_logout_button)

        kakaoLoginButton.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        val kakaoUnlinkButton = findViewById<Button>(R.id.kakao_unlink_button)

        kakaoUnlinkButton.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }
    }
}