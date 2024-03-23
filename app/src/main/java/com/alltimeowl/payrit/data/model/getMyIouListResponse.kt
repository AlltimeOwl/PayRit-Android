package com.alltimeowl.payrit.data.model

data class getMyIouListResponse(
    val paperId: Int,
    val paperRole: String,
    val transactionDate: String,
    val repaymentStartDate: String,
    val repaymentEndDate: String,
    val amount: Int,
    val paperStatus: String,
    val peerName: String,
    val dueDate: Int,
    val repaymentRate: Double,
    val isWriter: Boolean,
)
