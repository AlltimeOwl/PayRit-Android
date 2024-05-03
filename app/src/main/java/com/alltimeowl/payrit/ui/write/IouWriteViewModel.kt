package com.alltimeowl.payrit.ui.write

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.network.repository.IouWriteRepository

class IouWriteViewModel: ViewModel() {

    private val louWriteRepository = IouWriteRepository()

    private val _modifyData = MutableLiveData<IouWriteRequest>()
    val modifyData: LiveData<IouWriteRequest> = _modifyData

    fun iouWrite(accessToken: String, iouWriteRequest: IouWriteRequest) {
        louWriteRepository.iouWrite(accessToken, iouWriteRequest)
    }

    fun setModifyData(iouWriteRequest: IouWriteRequest) {
        _modifyData.value = iouWriteRequest
    }

    fun updateModifyTransactionData(
        newAmount: Int,
        newTransactionDate: String,
        newRepaymentStartDate: String,
        newRepaymentEndDate: String,
        newSpecialConditions: String,
        newInterestRate: Float,
        newInterestPaymentDate: Int,
        newInterest: Int
    ) {
        _modifyData.value?.let {
            val updatedData = it.copy(
                amount = newAmount,
                transactionDate = newTransactionDate,
                repaymentStartDate = newRepaymentStartDate,
                repaymentEndDate = newRepaymentEndDate,
                specialConditions = newSpecialConditions,
                interestRate = newInterestRate,
                interestPaymentDate = newInterestPaymentDate,
                interest = newInterest
            )
            _modifyData.value = updatedData
        }
    }
}