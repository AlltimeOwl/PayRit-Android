package com.alltimeowl.payrit.data.network.api

import com.alltimeowl.payrit.data.model.LoginRequest
import com.alltimeowl.payrit.data.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PayRitApi {
    @POST("api/v1/oauth/KAKAO")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}