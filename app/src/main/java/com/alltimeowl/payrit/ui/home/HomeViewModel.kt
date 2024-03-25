package com.alltimeowl.payrit.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.GetIouDetailResponse
import com.alltimeowl.payrit.data.model.MemoListResponse
import com.alltimeowl.payrit.data.model.MemoRequest
import com.alltimeowl.payrit.data.model.RepaymentHistory
import com.alltimeowl.payrit.data.model.RepaymentRequest
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.data.network.repository.IouRepository

class HomeViewModel : ViewModel() {

    private val iouRepository = IouRepository()

    private val _iouList = MutableLiveData<List<getMyIouListResponse>>()
    val iouList: LiveData<List<getMyIouListResponse>> = _iouList

    private val _iouDetail = MutableLiveData<GetIouDetailResponse>()
    val iouDetail: LiveData<GetIouDetailResponse> = _iouDetail

    private val _repaymentList = MutableLiveData<List<RepaymentHistory>>()
    val repaymentList: LiveData<List<RepaymentHistory>> = _repaymentList

    fun loadMyIouList(accessToken: String) {
        iouRepository.getMyIouList(accessToken) { iouList ->
            _iouList.value = iouList
        }
    }

    fun getIouDetail(accessToken: String, paperId: Int) {
        iouRepository.getIouDetail(accessToken, paperId) { response ->
            _iouDetail.postValue(response)
            response?.let {
                _repaymentList.postValue(it.repaymentHistories)
            }
        }
    }

    fun postRepayment(accessToken: String, repaymentRequest: RepaymentRequest, paperId: Int) {
        iouRepository.postRepayment(accessToken, repaymentRequest) { success ->
            if (success) {
                getIouDetail(accessToken, paperId)
            }
        }
    }

    fun postMemo(accessToken: String, paperId: Int, memoRequest: MemoRequest) {
        iouRepository.postMemo(accessToken, paperId, memoRequest)
    }

}