package com.heartsignal.hatalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat

import com.cometchat.pro.exceptions.CometChatException
import com.heartsignal.hatalk.signalRoom.PRIVATE.IDs
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.common.model.KakaoSdkError
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(com.heartsignal.hatalk.R.layout.activity_main) {
    private val TAG = "HEART"
    // 권한 설정


    /**
     * 매칭 반복을 위한 변수설정 [Matching]
     */

    private var matchingStatus by Delegates.notNull<Boolean>()

//    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val appID: String = IDs.APP_ID // Replace with your App ID
        val region: String = IDs.REGION // Replace with your App Region ("eu" or "us")

        val appSettings =
            AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region)
                .build()

        // Matching confirm 반복을 위한 변수

        matchingStatus = false

        CometChat.init(this, appID, appSettings, object : CometChat.CallbackListener<String>() {
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
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.heartsignal.hatalk.MainHomeActivity::class.java)
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
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == "AuthError(statusCode=302, reason=Unknown, response=AuthErrorResponse(error=NotSupportError, errorDescription=KakaoTalk is installed but not connected to Kakao account.))" -> {
                        Log.d("Kakao ", error.toString())
                        UserApiClient.instance.loginWithKakaoAccount(
                            this,
                            callback = accountCallBack
                        )
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.heartsignal.hatalk.MainHomeActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
                        } else {
                            UserApiClient.instance.loginWithKakaoAccount(
                                this,
                                callback = accountCallBack
                            )
                        }
                    } else {
                        Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, com.heartsignal.hatalk.MainHomeActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }

        val kakaoLoginButton = findViewById<ImageView>(com.heartsignal.hatalk.R.id.kako_login)

        kakaoLoginButton.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = accountCallBack)
            }
        }


    }

    /** [CometChat] to remove callListener */
    override fun onDestroy() {
        super.onDestroy()

        val listenerID:String="UNIQUE_LISTENER_ID"
//        CometChat.removeCallListener(listenerID)
//        /* Activity가 끝나거나, 혹시 모를 상황을 대비하여 false로 다시 처리 */
//        matchingStatus = false
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

}