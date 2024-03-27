package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.LoginRequest
import com.alltimeowl.payrit.data.model.LoginResponse
import com.alltimeowl.payrit.data.model.WithdrawalRequest
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

    fun loginKakao(accessToken: String, refreshToken: String, firebaseToken: String, callback: LoginResultCallback) {
        val loginRequest = LoginRequest(accessToken, refreshToken, firebaseToken)
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

    fun logoutUser(accessToken: String) {
        payRitApi.logoutUser("Bearer $accessToken").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("MyPageMainFragment", "성공시 response.code : ${response.code()}")
                } else {
                    Log.d("MyPageMainFragment", "errorResponse : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("MyPageMainFragment", "네트워크 오류: ${t.message}")
            }

        })
    }

    fun withdrawalUser(accessToken: String, withdrawalRequest: WithdrawalRequest) {
        payRitApi.withdrawalUser("Bearer $accessToken", withdrawalRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("WithdrawalFragment", "회원 탈퇴 성공시 response.code : ${response.code()}")
                } else {
                    Log.d("WithdrawalFragment", "회원 탈퇴 실패시 errorResponse : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("WithdrawalFragment", "네트워크 오류: ${t.message}")
            }

        })
    }

}