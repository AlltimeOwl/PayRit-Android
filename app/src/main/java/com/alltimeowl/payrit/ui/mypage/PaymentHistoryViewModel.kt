package com.alltimeowl.payrit.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.GetMyTransactionListResponse
import com.alltimeowl.payrit.data.model.GetTransactionDetailResponse
import com.alltimeowl.payrit.data.network.repository.PaymentRepository

class PaymentHistoryViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    private val _transactionList = MutableLiveData<List<GetMyTransactionListResponse>>()
    val transactionList: LiveData<List<GetMyTransactionListResponse>> = _transactionList

    private val _transactionDetail = MutableLiveData<GetTransactionDetailResponse>()
    val transactionDetail: LiveData<GetTransactionDetailResponse> = _transactionDetail

    fun getMyTransactionList(accessToken: String) {
        paymentRepository.getMyTransactionList(accessToken) { transactionList ->
            _transactionList.value = transactionList
        }
    }

    fun getTransactionDetail(accessToken: String, historyId: Int) {
        paymentRepository.getTransactionDetail(accessToken, historyId) { response ->
            _transactionDetail.value = response
        }
    }

}