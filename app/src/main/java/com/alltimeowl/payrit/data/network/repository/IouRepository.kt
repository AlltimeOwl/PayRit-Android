package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.ApprovalIouErrorResponse
import com.alltimeowl.payrit.data.model.GetIouDetailResponse
import com.alltimeowl.payrit.data.model.RepaymentErrorResponse
import com.alltimeowl.payrit.data.model.RepaymentRequest
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.data.network.api.PayRitApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IouRepository {

    private val payRitApi: PayRitApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://payrit.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        payRitApi = retrofit.create(PayRitApi::class.java)
    }

    fun getMyIouList(accessToken: String, callback: (List<getMyIouListResponse>) -> Unit) {
        payRitApi.getMyIouList("Bearer $accessToken").enqueue(object : Callback<List<getMyIouListResponse>> {
            override fun onResponse(call: Call<List<getMyIouListResponse>>, response: Response<List<getMyIouListResponse>>) {

                if (response.isSuccessful) {
                    callback(response.body() ?: emptyList())
                } else {
                    Log.d("HomeFragment", "Error: ${response.code()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<getMyIouListResponse>>, t: Throwable) {
                Log.d("HomeFragment", "네트워크 오류: ${t.message}")
                callback(emptyList())
            }
        })
    }

    fun getIouDetail(accessToken: String, paperId: Int, callback: (GetIouDetailResponse?) -> Unit) {
        payRitApi.getIouDetail("Bearer $accessToken", paperId).enqueue(object : Callback<GetIouDetailResponse> {
            override fun onResponse(call: Call<GetIouDetailResponse>, response: Response<GetIouDetailResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.d("RecipientApprovalFragment", "response.code : ${response.code()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GetIouDetailResponse>, t: Throwable) {
                Log.d("RecipientApprovalFragment", "네트워크 오류: ${t.message}")
                callback(null)
            }
        })
    }

    fun approvalIou(accessToken: String, paperId: Int) {
        payRitApi.approvalIou("Bearer $accessToken", paperId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("RecipientApprovalFragment", "성공시 response.body : ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: ApprovalIouErrorResponse? = gson.fromJson(errorBody, ApprovalIouErrorResponse::class.java)
                    Log.d("RecipientApprovalFragment", "errorResponse : ${errorResponse}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("RecipientApprovalFragment", "네트워크 오류: ${t.message}")
            }

        })
    }

    fun postRepayment(accessToken: String, repaymentRequest: RepaymentRequest) {
        payRitApi.postRepayment("Bearer $accessToken", repaymentRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("IouDetailAmountReceivedFragment", "성공시 response.body : ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: RepaymentErrorResponse? = gson.fromJson(errorBody, RepaymentErrorResponse::class.java)
                    Log.d("IouDetailAmountReceivedFragment", "errorResponse : ${errorResponse}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("IouDetailAmountReceivedFragment", "네트워크 오류: ${t.message}")
            }

        })
    }
}