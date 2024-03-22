package com.alltimeowl.payrit.data.model

data class IouWriteRequest(
    val writerRole : String,
    val amount : Int?,
    val calcedAmount: Int?,
    val transactionDate: String,
    val repaymentStartDate: String,
    val repaymentEndDate: String,
    val specialConditions: String?,
    val interestRate: Float?,
    val interestPaymentDate: Int?,
    val creditorName: String,
    val creditorPhoneNumber: String,
    val creditorAddress: String?,
    val debtorName: String,
    val debtorPhoneNumber: String,
    val debtorAddress: String?,
)
