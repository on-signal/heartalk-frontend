package com.heartsignal.hatalk

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(com.heartsignal.hatalk.R.string.kakao_native_app_key))
    }
}