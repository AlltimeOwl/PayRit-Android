package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.ApprovalIouErrorResponse
import com.alltimeowl.payrit.data.model.PromiseDetail
import com.alltimeowl.payrit.data.model.WritePromiseRequest
import com.alltimeowl.payrit.data.network.api.PayRitApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PromiseRepository {

    private val payRitApi: PayRitApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://payrit.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        payRitApi = retrofit.create(PayRitApi::class.java)
    }

    fun writePromise(
        accessToken: String,
        writePromiseRequest: WritePromiseRequest,
        onSuccess: (Long) -> Unit,
        onFailure: (Boolean) -> Unit
    ) {
        payRitApi.writePromise("Bearer $accessToken", writePromiseRequest)
            .enqueue(object : Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    if (response.isSuccessful) {
                        Log.d("PromiseMakeFragment", "writePromise 성공시 response.code : ${response.code()}")
                        Log.d("PromiseMakeFragment", "writePromise 성공시 response.body : ${response.body()}")
                        response.body()?.let { onSuccess(it) }
                    } else {
                        Log.d("PromiseMakeFragment", "writePromise 실패시 response.code : ${response.code()}")
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse: ApprovalIouErrorResponse? = gson.fromJson(errorBody, ApprovalIouErrorResponse::class.java)
                        Log.d("PromiseMakeFragment", "errorResponse : ${errorResponse}")

                        onFailure(false)
                    }
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    Log.d("PromiseMakeFragment", "writePromise 네트워크 오류: ${t.message}")
                    onFailure(false)
                }

            })
    }

    fun getMyPromiseList(accessToken: String, callback: (List<PromiseDetail>) -> Unit) {
        payRitApi.getMyPromiseList("Bearer $accessToken").enqueue(object : Callback<List<PromiseDetail>> {
            override fun onResponse(call: Call<List<PromiseDetail>>, response: Response<List<PromiseDetail>>) {
                if (response.isSuccessful) {
                    Log.d("getMyPromiseList", "getMyPromiseList 성공시 response.code : ${response.code()}")
                    callback(response.body() ?: emptyList())
                } else {
                    Log.d("getMyPromiseList", "getMyPromiseList 실패시 response.code : ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: ApprovalIouErrorResponse? = gson.fromJson(errorBody, ApprovalIouErrorResponse::class.java)
                    Log.d("getMyPromiseList", "errorResponse : ${errorResponse}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<PromiseDetail>>, t: Throwable) {
                Log.d("getMyPromiseList", "getMyPromiseList 네트워크 오류: ${t.message}")
                callback(emptyList())
            }


        })
    }

    fun deletePromise(
        accessToken: String,
        id: Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Boolean) -> Unit
    ) {
        payRitApi.deletePromise("Bearer $accessToken", id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("HomePromiseInfoFragment", "deletePromise 성공시 response.code : ${response.code()}")
                    onSuccess(true)
                } else {
                    Log.d("HomePromiseInfoFragment", "deletePromise 실패시 response.code : ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: ApprovalIouErrorResponse? = gson.fromJson(errorBody, ApprovalIouErrorResponse::class.java)
                    Log.d("HomePromiseInfoFragment", "errorResponse : ${errorResponse}")
                    onFailure(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("HomePromiseInfoFragment", "deletePromise 네트워크 오류: ${t.message}")
                onFailure(false)
            }

        })
    }

    fun sharePromise(accessToken: String, id: Int, onSuccess: (Boolean) -> Unit) {
        payRitApi.sharePromise("Bearer $accessToken", id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("HomeFragment", "sharePromise 성공시 response.code : ${response.code()}")
                    onSuccess(true)
                } else {
                    Log.d("HomeFragment", "sharePromise 실패시 response.code : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("HomeFragment", "sharePromise 네트워크 오류: ${t.message}")
            }

        })
    }
}