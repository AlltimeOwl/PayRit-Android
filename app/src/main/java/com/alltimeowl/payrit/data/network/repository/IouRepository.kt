package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.data.network.api.PayRitApi
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
}