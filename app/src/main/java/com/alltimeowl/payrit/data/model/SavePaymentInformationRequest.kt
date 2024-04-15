package com.alltimeowl.payrit.data.model

data class SavePaymentInformationRequest(
    val paperId : Int,
    val transactionDate : String,
    val amount: Int,
    val transactionType: String,
    val impUid: String,
    val merchantUid: String,
    val isSuccess: Boolean
)
