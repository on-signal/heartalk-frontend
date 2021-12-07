package com.heartsignal.hatalk

import android.app.Application
import com.heartsignal.hatalk.main.data.Partner
import com.heartsignal.hatalk.model.userInfo
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    companion object {
        lateinit var userInfo: userInfo
        var tempPartner: Partner? = null
    }

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(com.heartsignal.hatalk.R.string.kakao_native_app_key))
    }
}