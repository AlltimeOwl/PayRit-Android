package com.alltimeowl.payrit.data.network.api

import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.IouWriteResponse
import com.alltimeowl.payrit.data.model.LoginRequest
import com.alltimeowl.payrit.data.model.LoginResponse
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PayRitApi {
    @POST("api/v1/oauth/KAKAO")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/v1/paper/write")
    fun iouWrite(
        @Header("Authorization") accessToken: String,
        @Body request: IouWriteRequest): Call<IouWriteResponse>
    
    @GET("api/v1/paper/list")
    fun getMyIouList(@Header("Authorization") accessToken: String): Call<List<getMyIouListResponse>>
}