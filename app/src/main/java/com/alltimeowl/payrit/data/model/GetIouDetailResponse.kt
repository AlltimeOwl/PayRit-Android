package com.alltimeowl.payrit.data.model

data class GetIouDetailResponse(
    val paperId: Int,
    val paperUrl: String,
    val memberRole: String,
    val primeAmount: Int,
    val interest: Int,
    val amount: Int,
    val remainingAmount: Int,
    val interestRate: Float,
    val interestPaymentDate: Int,
    val repaymentRate: Double,
    val repaymentStartDate: String,
    val repaymentEndDate: String,
    val transactionDate: String,
    val dueDate: Int,
    val creditorName: String,
    val creditorPhoneNumber: String,
    val creditorAddress: String,
    val debtorName: String,
    val debtorPhoneNumber: String,
    val debtorAddress: String,
    val specialConditions: String,
    val memoListResponses: List<MemoListResponse>,
    val repaymentHistories: List<RepaymentHistory>
)

data class MemoListResponse(
    val id: Int,
    val content: String,
    val createdAt: String
)

data class RepaymentHistory(
    val id: Int,
    val repaymentDate: String,
    val repaymentAmount: Int
)
