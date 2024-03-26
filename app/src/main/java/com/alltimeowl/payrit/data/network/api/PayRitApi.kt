package com.alltimeowl.payrit.data.network.api

import com.alltimeowl.payrit.data.model.GetIouDetailResponse
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.IouWriteResponse
import com.alltimeowl.payrit.data.model.LoginRequest
import com.alltimeowl.payrit.data.model.LoginResponse
import com.alltimeowl.payrit.data.model.MemoRequest
import com.alltimeowl.payrit.data.model.RepaymentRequest
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PayRitApi {
    @POST("api/v1/oauth/KAKAO")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/v1/paper/write")
    fun iouWrite(
        @Header("Authorization") accessToken: String,
        @Body request: IouWriteRequest): Call<IouWriteResponse>
    
    @GET("api/v1/paper/list")
    fun getMyIouList(@Header("Authorization") accessToken: String): Call<List<getMyIouListResponse>>

    @GET("api/v1/paper/{id}")
    fun getIouDetail(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Call<GetIouDetailResponse>

    @PUT("api/v1/paper/approve/accept/{id}")
    fun approvalIou(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Call<Void>

    @POST("api/v1/paper/repayment/request")
    fun postRepayment(
        @Header("Authorization") accessToken: String,
        @Body request : RepaymentRequest
    ): Call<Void>

    @POST("api/v1/memo/{paperId}")
    fun postMemo(
        @Header("Authorization") accessToken: String,
        @Path("paperId") paperId: Int,
        @Body memoRequest: MemoRequest
    ): Call<Void>
}