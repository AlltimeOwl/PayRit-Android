package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.GetPaymentInformationErrorResponse
import com.alltimeowl.payrit.data.model.GetPaymentInformationResponse
import com.alltimeowl.payrit.data.model.SavePaymentInformationRequest
import com.alltimeowl.payrit.data.network.api.PayRitApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PaymentRepository {

    private val payRitApi: PayRitApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://payrit.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        payRitApi = retrofit.create(PayRitApi::class.java)
    }

    interface GetPaymentInformationCallback {
        fun onSuccess(getPaymentInformationResponse : GetPaymentInformationResponse?)
    }

    fun getPaymentInformation(accessToken: String, paperId: Int, callback: GetPaymentInformationCallback) {
        payRitApi.getPaymentInformation("Bearer $accessToken", paperId).enqueue(object : Callback<GetPaymentInformationResponse> {
            override fun onResponse(
                call: Call<GetPaymentInformationResponse>,
                response: Response<GetPaymentInformationResponse>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: GetPaymentInformationErrorResponse? = gson.fromJson(errorBody, GetPaymentInformationErrorResponse::class.java)
                    Log.d("PaymentFragment", "errorResponse : ${errorResponse}")
                }
            }

            override fun onFailure(call: Call<GetPaymentInformationResponse>, t: Throwable) {
                Log.d("PaymentFragment", "네트워크 오류: ${t.message}")
            }


        })
    }

    interface SavePaymentInformationCallback {
        fun onSuccess()
        fun onFailure()
    }

    fun savePaymentInformation(accessToken: String, savePaymentInformationRequest: SavePaymentInformationRequest, callback: SavePaymentInformationCallback) {
        payRitApi.savePaymentInformation("Bearer $accessToken", savePaymentInformationRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("PaymentFragment", "savePaymentInformation 성공 response.code : ${response.code()}")
                    callback.onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: GetPaymentInformationErrorResponse? = gson.fromJson(errorBody, GetPaymentInformationErrorResponse::class.java)
                    Log.d("PaymentFragment", "savePaymentInformation 실패 errorResponse : ${errorResponse}")
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("PaymentFragment", "savePaymentInformation 네트워크 오류: ${t.message}")
                callback.onFailure()
            }

        })
    }
}