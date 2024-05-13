package com.alltimeowl.payrit.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.ActivityLoginBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    private var backPressedTime: Long = 0
    private val backPressedInterval = 2000 // 뒤로가기 버튼을 두 번 누르는 간격 (2초)

    lateinit var viewModel: LoginViewModel

    private var firebaseToken = ""

    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            // 로그인 실패
            Log.d(TAG, "로그인 실패 error : ${error}")
        } else if (token != null) {
            // 로그인 성공

            // 사용자 정보 요청
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    val name = user.kakaoAccount?.name // 사용자의 실제 이름 (이름 정보에 접근 권한이 필요합니다)
                    val phoneNumber = user.kakaoAccount?.phoneNumber
                    val email = user.kakaoAccount?.email

                    SharedPreferencesManager.saveUserInfo(name, phoneNumber, email)
                }
            }

            Firebase.messaging.token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseToken= task.result

                    // 토큰을 가져온 후에 로그인 요청을 진행
                    viewModel.loginKakao(token.accessToken, token.refreshToken, firebaseToken)

                } else {
                    // 토큰을 가져오는 데 실패한 경우에 대한 처리
                }
            }

            // ViewModel의 loginResult 관찰
            viewModel.loginResult.observe(this) { result ->
                result.fold(onSuccess = {
                    SharedPreferencesManager.saveUserToken(it.accessToken, it.refreshToken, firebaseToken)

                    // 서버 로그인 성공 시 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, onFailure = {
                    // 서버 로그인 실패 시 오류 메시지 표시
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
            }

        }
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val TAG = "LoginActivity1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        firebaseAnalytics = Firebase.analytics

        moveToHome()
    }

    private fun moveToHome() {
        binding.buttonKakaotalkLogin.setOnClickListener {

            // 이벤트 번들 생성
            val bundle = Bundle()

            // 이벤트 기록
            firebaseAnalytics.logEvent("kakao_signIn_AOS", bundle)

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

                        // 사용자 정보 요청
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e(TAG, "사용자 정보 요청 실패", error)
                            } else if (user != null) {
                                val name = user.kakaoAccount?.name // 사용자의 실제 이름 (이름 정보에 접근 권한이 필요합니다)
                                val phoneNumber = user.kakaoAccount?.phoneNumber
                                val email = user.kakaoAccount?.email

                                SharedPreferencesManager.saveUserInfo(name, phoneNumber, email)
                            }
                        }

                        Firebase.messaging.token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseToken= task.result

                                // 토큰을 가져온 후에 로그인 요청을 진행
                                viewModel.loginKakao(token.accessToken, token.refreshToken, firebaseToken)

                            } else {
                                // 토큰을 가져오는 데 실패한 경우에 대한 처리
                            }
                        }

                        // ViewModel의 loginResult 관찰
                        viewModel.loginResult.observe(this) { result ->
                            result.fold(onSuccess = {

                                SharedPreferencesManager.saveUserToken(it.accessToken, it.refreshToken, firebaseToken)

                                // 서버 로그인 성공 시 MainActivity로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, onFailure = {
                                // 서버 로그인 실패 시 오류 메시지 표시
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                            })
                        }

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