package com.alltimeowl.payrit.data.model

data class RepaymentRequest(
    val paperId: Int,
    val repaymentDate: String,
    val repaymentAmount: Int,
)

data class RepaymentErrorResponse(
    val success: Boolean,
    val status: Int,
    val code: String,
    val reason: String,
    val timeStamp: String,
    val path: String
)