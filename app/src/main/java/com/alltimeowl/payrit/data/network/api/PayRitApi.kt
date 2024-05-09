package com.alltimeowl.payrit.data.network.api

import com.alltimeowl.payrit.data.model.CertificationInfoResponse
import com.alltimeowl.payrit.data.model.DeleteRepaymentRequest
import com.alltimeowl.payrit.data.model.GetIouDetailResponse
import com.alltimeowl.payrit.data.model.GetMyTransactionListResponse
import com.alltimeowl.payrit.data.model.GetPaymentInformationResponse
import com.alltimeowl.payrit.data.model.GetTransactionDetailResponse
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.IouWriteResponse
import com.alltimeowl.payrit.data.model.LoginRequest
import com.alltimeowl.payrit.data.model.LoginResponse
import com.alltimeowl.payrit.data.model.MemoRequest
import com.alltimeowl.payrit.data.model.ModifyRequest
import com.alltimeowl.payrit.data.model.PromiseDetail
import com.alltimeowl.payrit.data.model.RepaymentRequest
import com.alltimeowl.payrit.data.model.SavePaymentInformationRequest
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.data.model.UserCertificationResponse
import com.alltimeowl.payrit.data.model.WritePromiseRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PayRitApi {
    @POST("api/v1/oauth/KAKAO")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/v1/oauth/logout")
    fun logoutUser(@Header("Authorization") accessToken: String): Call<Void>

    @DELETE("api/v1/oauth/revoke")
    fun withdrawalUser(
        @Header("Authorization") accessToken: String
    ): Call<Void>

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

    @Multipart
    @PUT("api/v1/paper/approve/accept/{id}")
    fun approvalIou(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int,
        @Part file: MultipartBody.Part
    ): Call<Void>

    @POST("api/v1/paper/repayment/request")
    fun postRepayment(
        @Header("Authorization") accessToken: String,
        @Body request : RepaymentRequest
    ): Call<Void>

    @POST("api/v1/paper/repayment/cancel")
    fun deleteRepayment(
        @Header("Authorization") accessToken: String,
        @Body deleteRepaymentRequest: DeleteRepaymentRequest
    ): Call<Void>

    @POST("api/v1/memo/{paperId}")
    fun postMemo(
        @Header("Authorization") accessToken: String,
        @Path("paperId") paperId: Int,
        @Body memoRequest: MemoRequest
    ): Call<Void>

    @DELETE("api/v1/memo/{memoId}")
    fun deleteMemo(
        @Header("Authorization") accessToken: String,
        @Path("memoId") memoId: Int
    ): Call<Void>

    @GET("api/v1/oauth/check")
    fun checkCertification(@Header("Authorization") accessToken: String): Call<Void>

    @POST("api/v1/oauth/certification/init")
    fun userCertification(
        @Header("Authorization") accessToken: String,
        @Body userCertificationResponse: UserCertificationResponse
    ): Call<Void>

    @POST("api/v1/transaction/paymentInfo/{paperId}/PAPER_TRANSACTION")
    fun getPaymentInformation(
        @Header("Authorization") accessToken: String,
        @Path("paperId") paperId: Int
    ): Call<GetPaymentInformationResponse>

    @POST("api/v1/transaction/save")
    fun savePaymentInformation(
        @Header("Authorization") accessToken: String,
        @Body savePaymentInformationRequest: SavePaymentInformationRequest
    ): Call<Void>

    @GET("api/v1/profile/certification")
    fun getCertificationInfo(@Header("Authorization") accessToken: String): Call<CertificationInfoResponse>

    @GET("api/v1/transaction/list")
    fun getMyTransactionList(@Header("Authorization") accessToken: String): Call<List<GetMyTransactionListResponse>>

    @GET("api/v1/transaction/detail/{id}")
    fun getTransactionDetail(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Call<GetTransactionDetailResponse>

    @POST("api/v1/paper/reload")
    fun reloadIou(@Header("Authorization") accessToken: String): Call<Void>

    @POST("api/v1/paper/modify/request")
    fun modifyIouRequest(
        @Header("Authorization") accessToken: String,
        @Body modifyRequest: ModifyRequest
    ): Call<Void>

    @PUT("api/v1/paper/modify/accept/{id}")
    fun modifyAcceptIou(
        @Header("Authorization") accessToken: String,
        @Body iouWriteRequest: IouWriteRequest,
        @Path("id") id: Int
    ): Call<Void>

    @PUT("api/v1/paper/refuse/{id}")
    fun refuseIou(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Call<Void>

    @POST("api/v1/promise/write")
    fun writePromise(
        @Header("Authorization") accessToken: String,
        @Body writePromiseRequest: WritePromiseRequest
    ): Call<Long>

    @GET("api/v1/promise/list")
    fun getMyPromiseList(@Header("Authorization") accessToken: String): Call<List<PromiseDetail>>

    @DELETE("api/v1/promise/remove/{id}")
    fun deletePromise(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Call<Void>
}