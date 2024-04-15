package com.alltimeowl.payrit.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.GetPaymentInformationResponse
import com.alltimeowl.payrit.data.model.SavePaymentInformationRequest
import com.alltimeowl.payrit.data.network.repository.PaymentRepository

class PaymentViewModel: ViewModel() {

    private val paymentRepository = PaymentRepository()

    private val _getPaymentInformationResult = MutableLiveData<GetPaymentInformationResponse?>()
    val getPaymentInformationResult: LiveData<GetPaymentInformationResponse?> = _getPaymentInformationResult

    private val _savePaymentInformationResult = MutableLiveData<Boolean>()
    val savePaymentInformationResult: LiveData<Boolean> = _savePaymentInformationResult

    fun getPaymentInformation(accessToken: String, paperId: Int) {
        paymentRepository.getPaymentInformation(accessToken, paperId, object : PaymentRepository.GetPaymentInformationCallback {
            override fun onSuccess(getPaymentInformationResponse: GetPaymentInformationResponse?) {
                _getPaymentInformationResult.value = getPaymentInformationResponse
            }

        })
    }

    fun savePaymentInformation(accessToken: String, savePaymentInformationRequest: SavePaymentInformationRequest) {
        paymentRepository.savePaymentInformation(accessToken, savePaymentInformationRequest, object : PaymentRepository.SavePaymentInformationCallback {
            override fun onSuccess() {
                _savePaymentInformationResult.value = true
            }

            override fun onFailure() {
                _savePaymentInformationResult.value = false
            }

        })
    }

}