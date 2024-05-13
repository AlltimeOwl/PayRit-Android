package com.alltimeowl.payrit.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.ActivitySplashBinding
import com.alltimeowl.payrit.ui.login.LoginActivity
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        firebaseAnalytics = Firebase.analytics
        setContentView(binding.root)

        val bundle = Bundle()
        firebaseAnalytics.logEvent("visit_PayRit_AOS", bundle)

        Handler(Looper.getMainLooper()).postDelayed({

            val accessToken = SharedPreferencesManager.getAccessToken()

            if (accessToken.isNotEmpty()) {
                // 저장된 accessToken이 있다면, 로그인 상태 유지
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // 저장된 accessToken이 없다면, 로그인 화면으로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            finish()

        }, 2000)

    }
}