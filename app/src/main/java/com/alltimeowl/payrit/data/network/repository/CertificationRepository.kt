package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.CertificationInfoResponse
import com.alltimeowl.payrit.data.model.UserCertificationResponse
import com.alltimeowl.payrit.data.network.api.PayRitApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CertificationRepository {

    private val payRitApi: PayRitApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://payrit.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        payRitApi = retrofit.create(PayRitApi::class.java)
    }


    fun checkCertification(
        accessToken: String,
        onSuccess: (Int) -> Unit,
        onFailure: (Int?) -> Unit
    ) {
        payRitApi.checkCertification("Bearer $accessToken").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {
                    Log.d("WriteMainFragment", "성공: ${response.code()}")
                    onSuccess.invoke(response.code())
                } else {
                    Log.d("WriteMainFragment", "errorCode: ${response.code()}")
                    onFailure.invoke(response.code())
                }

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("WriteMainFragment", "네트워크 오류: ${t.message}")
                onFailure.invoke(null)
            }

        })
    }

    fun userCertification(accessToken: String, userCertificationResponse: UserCertificationResponse) {
        payRitApi.userCertification("Bearer $accessToken", userCertificationResponse).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("WriteMainFragment", "certification 성공: ${response.code()}")
                } else {
                    Log.d("WriteMainFragment", "certification errorCode: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("WriteMainFragment", "certification 네트워크 오류: ${t.message}")
            }

        })
    }

    fun getCertificationInfo(accessToken: String, callback: (CertificationInfoResponse) -> Unit) {
        payRitApi.getCertificationInfo("Bearer $accessToken").enqueue(object : Callback<CertificationInfoResponse> {
            override fun onResponse(call: Call<CertificationInfoResponse>, response: Response<CertificationInfoResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(it) }
                } else {
                    Log.d("CertificationInfoFragment", "getCertificationInfo errorCode: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CertificationInfoResponse>, t: Throwable) {
                Log.d("CertificationInfoFragment", "getCertificationInfo 네트워크 오류: ${t.message}")
            }

        })
    }
}