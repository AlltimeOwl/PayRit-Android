package com.alltimeowl.payrit.data.network.repository

import com.alltimeowl.payrit.data.model.LoginRequest
import com.alltimeowl.payrit.data.model.LoginResponse
import com.alltimeowl.payrit.data.network.api.PayRitApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginRepository {

    private val payRitApi: PayRitApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://payrit.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        payRitApi = retrofit.create(PayRitApi::class.java)
    }

    interface LoginResultCallback {
        fun onSuccess(accessToken: String?, refreshToken: String?)
        fun onError(error: String)
    }

    fun loginKakao(accessToken: String, refreshToken: String, callback: LoginResultCallback) {
        val loginRequest = LoginRequest(accessToken, refreshToken)
        val call = payRitApi.loginUser(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()?.accessToken, response.body()?.refreshToken)
                } else {
                    callback.onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onError("네트워크 오류: ${t.message}")
            }
        })
    }

}