package com.alltimeowl.payrit.data.model

data class GetPaymentInformationResponse(
    val PID: String,
    val PGCode: String,
    val merchantUID: String,
    val name: String,
    val amount: Int,
    val buyerEmail: String,
    val buyerName: String,
    val buyerTel: String
)
