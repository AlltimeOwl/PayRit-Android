package com.alltimeowl.payrit.data.model

data class GetTransactionDetailResponse(
    val historyId: Int,
    val transactionDate: String,
    val applyNum: String,
    val paymentMethod: String,
    val amount: Int
)
