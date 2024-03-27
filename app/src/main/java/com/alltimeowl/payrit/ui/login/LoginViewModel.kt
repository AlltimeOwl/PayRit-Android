package com.alltimeowl.payrit.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.network.repository.LoginRepository

class LoginViewModel: ViewModel() {

    private val loginRepository = LoginRepository()

    // 로그인 결과를 나타내는 LiveData의 타입을 Result<LoginSuccess>로 변경
    private val _loginResult = MutableLiveData<Result<LoginSuccess>>()
    val loginResult: LiveData<Result<LoginSuccess>> = _loginResult

    fun loginKakao(accessToken: String, refreshToken: String, firebaseToken: String) {
        loginRepository.loginKakao(accessToken, refreshToken, firebaseToken, object : LoginRepository.LoginResultCallback {
            override fun onSuccess(accessToken: String?, refreshToken: String?) {
                _loginResult.value = Result.success(LoginSuccess(accessToken, refreshToken))
            }

            override fun onError(error: String) {
                _loginResult.value = Result.failure(Exception(error))
            }
        })
    }
}

data class LoginSuccess(val accessToken: String?, val refreshToken: String?)