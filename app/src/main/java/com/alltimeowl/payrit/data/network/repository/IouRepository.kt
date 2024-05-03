package com.alltimeowl.payrit.data.network.repository

import android.util.Log
import com.alltimeowl.payrit.data.model.ApprovalIouErrorResponse
import com.alltimeowl.payrit.data.model.DeleteRepaymentRequest
import com.alltimeowl.payrit.data.model.GetIouDetailResponse
import com.alltimeowl.payrit.data.model.GetPaymentInformationErrorResponse
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.MemoRequest
import com.alltimeowl.payrit.data.model.ModifyRequest
import com.alltimeowl.payrit.data.model.RepaymentErrorResponse
import com.alltimeowl.payrit.data.model.RepaymentRequest
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.data.network.api.PayRitApi
import com.google.gson.Gson
import okhttp3.MultipartBody
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

    fun approvalIou(accessToken: String, paperId: Int, file: MultipartBody.Part) {
        payRitApi.approvalIou("Bearer $accessToken", paperId, file).enqueue(object : Callback<Void> {
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

    fun postRepayment(accessToken: String, repaymentRequest: RepaymentRequest, callback: (Boolean) -> Unit) {
        payRitApi.postRepayment("Bearer $accessToken", repaymentRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("IouDetailAmountReceivedFragment", "성공시 response.body : ${response.body()}")
                    callback(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: RepaymentErrorResponse? = gson.fromJson(errorBody, RepaymentErrorResponse::class.java)
                    Log.d("IouDetailAmountReceivedFragment", "errorResponse : ${errorResponse}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("IouDetailAmountReceivedFragment", "네트워크 오류: ${t.message}")
                callback(false)
            }
        })
    }

    fun deleteRepayment(accessToken: String, deleteRepaymentRequest: DeleteRepaymentRequest, callback: (Boolean) -> Unit) {
        payRitApi.deleteRepayment("Bearer $accessToken", deleteRepaymentRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("IouDetailAmountReceivedFragment", "deleteRepayment 성공시 response.code : ${response.code()}")
                    callback(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val gson = Gson()
                    val errorResponse: RepaymentErrorResponse? = gson.fromJson(errorBody, RepaymentErrorResponse::class.java)
                    Log.d("IouDetailAmountReceivedFragment", "deleteRepayment errorResponse : ${errorResponse}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("IouDetailAmountReceivedFragment", "네트워크 오류: ${t.message}")
                callback(false)
            }

        })
    }

    fun postMemo(accessToken: String, paperId: Int, memoRequest: MemoRequest, callback: (Boolean) -> Unit) {
        payRitApi.postMemo("Bearer $accessToken", paperId, memoRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("IouDetailMemoFragment", "성공시 response.code : ${response.code()}")
                    callback(true)
                } else {
                    Log.d("IouDetailMemoFragment", "errorResponse : ${response.code()}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("IouDetailMemoFragment", "네트워크 오류: ${t.message}")
                callback(false)
            }

        })


    }

    fun deleteMemo(accessToken: String, memoId: Int) {
        payRitApi.deleteMemo("Bearer $accessToken", memoId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("IouDetailMemoFragment", "deleteMemo 성공시 response.code : ${response.code()}")
                } else {
                    Log.d("IouDetailMemoFragment", "deleteMemo errorResponse : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("IouDetailMemoFragment", "네트워크 오류: ${t.message}")
            }

        })
    }

    fun reloadIou(accessToken: String) {
        payRitApi.reloadIou("Bearer $accessToken").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("WriteMainFragment", "reloadIou 성공시 response.code : ${response.code()}")
                } else {
                    Log.d("WriteMainFragment", "reloadIou 실패시 response.code : ${response.code()}")
                    Log.d("WriteMainFragment", "reloadIou 실패시 response.code : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("WriteMainFragment", "네트워크 오류: ${t.message}")
            }

        })
    }

    fun modifyIouRequest(
        accessToken: String,
        modifyRequest: ModifyRequest,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Boolean) -> Unit
    ) {
        payRitApi.modifyIouRequest("Bearer $accessToken", modifyRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("RecipientApprovalFragment", "modifyIouRequest 성공시 response.code : ${response.code()}")
                    onSuccess.invoke(true)
                } else {
                    Log.d("RecipientApprovalFragment", "modifyIouRequest 실패시 response.code : ${response.code()}")
                    Log.d("RecipientApprovalFragment", "modifyIouRequest 실패시 response.code : ${response.errorBody()}")
                    onFailure.invoke(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("RecipientApprovalFragment", "네트워크 오류: ${t.message}")
                onFailure.invoke(false)
            }

        })
    }

    fun modifyAcceptIou(accessToken: String, iouWriteRequest: IouWriteRequest, id:Int) {
        payRitApi.modifyAcceptIou("Bearer $accessToken", iouWriteRequest, id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("IouContentCheckFragment", "modifyAcceptIou 성공시 response.code : ${response.code()}")
                } else {
                    Log.d("IouContentCheckFragment", "modifyAcceptIou 실패시 response.code : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("IouContentCheckFragment", "modifyAcceptIou 네트워크 오류: ${t.message}")
            }

        })
    }

}