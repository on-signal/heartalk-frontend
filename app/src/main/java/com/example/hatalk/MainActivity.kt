package com.example.hatalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat

import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.hatalk.signalRoom.PRIVATE.IDs
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.common.model.KakaoSdkError
import com.example.hatalk.signalRoom.sigRoom.SignalRoomActivity as SignalRoomActivity


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val TAG = "HEART"
//    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appID:String= IDs.APP_ID // Replace with your App ID
        val region:String=IDs.REGION // Replace with your App Region ("eu" or "us")

        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build()


        CometChat.init(this,appID,appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d(TAG, "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Initialization failed with exception: " + p0?.message)
            }
        })

//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController
//
//        setupActionBarWithNavController(navController)
        val accountCallBack: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
//                        Log.d("Kakao ", error.toString())
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == "AuthError(statusCode=302, reason=Unknown, response=AuthErrorResponse(error=NotSupportError, errorDescription=KakaoTalk is installed but not connected to Kakao account.))" -> {
                        Log.d("Kakao ", error.toString())
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = accountCallBack)
                    }
                    else -> { // Unknown
//                        Log.d("Kakao ", error.toString())
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
                        }else{
                            UserApiClient.instance.loginWithKakaoAccount(this, callback = accountCallBack)
                        }
                    } else {
                        Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoadingActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }

        val kakaoLoginButton =  findViewById<ImageView>(R.id.kako_login)

        kakaoLoginButton.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = accountCallBack)
            }
        }

        val cometLogin = findViewById<Button>(R.id.comet_login)
        val apiKey:String ="e102b08b6600393cada117184230401d677a4517"

        cometLogin.setOnClickListener {
            val UID:String = findViewById<EditText>(R.id.login_edit).text.toString()
            CometChat.login(UID,apiKey, object : CometChat.CallbackListener<User>() {
                override fun onSuccess(p0: User?) {
                    Log.d(TAG, "Login Successful : " + p0?.toString())
                }
                override fun onError(p0: CometChatException?) {
                    Log.d(TAG, "Login failed with exception: " +  p0?.message)
                }
            })
        }

        val initCall = findViewById<Button>(R.id.initiate_call)
        initCall.setOnClickListener {
            goToSigRoom()
        }
    }

    private fun goToSigRoom() {
        val intent = Intent(this, SignalRoomActivity::class.java)
        startActivity(intent)
    }



    /** [CometChat] to remove callListener */
    override fun onDestroy() {
        super.onDestroy()

        val listenerID:String="UNIQUE_LISTENER_ID"
        CometChat.removeCallListener(listenerID)
    }






//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

}