package com.alltimeowl.payrit.data.model

data class GetPaymentInformationErrorResponse(
    val success: Boolean,
    val status: Int,
    val code: String,
    val reason: String,
    val timeStamp: Int,
    val path: String
)
