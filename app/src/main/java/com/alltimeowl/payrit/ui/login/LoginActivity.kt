package com.alltimeowl.payrit.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alltimeowl.payrit.databinding.ActivityLoginBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    private var backPressedTime: Long = 0
    private val backPressedInterval = 2000 // 뒤로가기 버튼을 두 번 누르는 간격 (2초)

    private val callback: (OAuthToken?, Throwable?) -> Unit = { toekn, error ->
        if (error != null) {
            // 로그인 실패
            Log.d(TAG, "로그인 실패 error : ${error}")
        } else if (toekn != null) {
            // 로그인 성공
            Log.d(TAG, "login in with kakako account token : $toekn")

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // LoginActivity 종료 (스택 제거)
            finish()
        }
    }

    val TAG = "LoginActivity1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moveToHome()
    }

    private fun moveToHome() {
        binding.buttonKakaotalkLogin.setOnClickListener {

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                // 카카오톡으로 로그인
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        // 카카오톡 로그인 실패
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            Log.d(TAG, "카카오톡 로그인 실패 : ${error.reason}")
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    }
                    else if (token != null) {
                        // 카카오톡 로그인 성공
                        Log.d(TAG, "token : ${token}")

                        val accessToken = token.accessToken
                        val refreshToken = token.refreshToken

                        Log.d(TAG, "accessToken : ${accessToken}")
                        Log.d(TAG, "refreshToken : ${refreshToken}")

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                }

            }  else {
                // 카카오계정 로그인
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }

        }

    }

    // 뒤로가기 버튼 이벤트 처리
    override fun onBackPressed() {
        if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity() // 앱 종료
        } else {
            Toast.makeText(this, "뒤로 가기를 한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}