package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.IouWriteResponse
import com.alltimeowl.payrit.data.network.api.PayRitApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IouWriteRepository {

    private val payRitApi: PayRitApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://payrit.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        payRitApi = retrofit.create(PayRitApi::class.java)
    }

    fun iouWrite(accessToken: String, iouWriteRequest: IouWriteRequest) {
        payRitApi.iouWrite("Bearer $accessToken", iouWriteRequest).enqueue(object : Callback<IouWriteResponse> {
            override fun onResponse(call: Call<IouWriteResponse>, response: Response<IouWriteResponse>) {
                if (response.isSuccessful) {
                    Log.d("IouContentCheckFragment", "성공: ${response.code()}")
                    Log.d("IouContentCheckFragment", "iouWriteRequest: ${iouWriteRequest}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse : IouWriteResponse? = gson.fromJson(errorBody, IouWriteResponse::class.java)
                    Log.d("IouContentCheckFragment", "errorResponse : ${errorResponse}")
                }
            }

            override fun onFailure(call: Call<IouWriteResponse>, t: Throwable) {
                Log.d("IouContentCheckFragment", "네트워크 오류: ${t.message}")
            }

        })


    }

}