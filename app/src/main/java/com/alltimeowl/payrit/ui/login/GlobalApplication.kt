package com.alltimeowl.payrit.ui.login

import android.app.Application
import com.alltimeowl.payrit.BuildConfig
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.iamport.sdk.domain.core.Iamport
import com.kakao.sdk.common.KakaoSdk


class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        SharedPreferencesManager.init(this)

        Iamport.create(this)
    }
}