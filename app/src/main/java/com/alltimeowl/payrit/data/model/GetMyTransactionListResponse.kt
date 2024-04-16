package com.alltimeowl.payrit.data.model

data class GetMyTransactionListResponse(
    val historyId: Int,
    val transactionDate: String,
    val amount: Int,
    val paymentMethod: String,
    val isSuccess: Boolean
)
