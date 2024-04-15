package com.alltimeowl.payrit.data.model

data class GetIouDetailResponse(
    val paperId: Int,
    val memberRole: String,
    val paperFormInfo: PaperFormInfo,
    val repaymentRate: Double,
    val dueDate: Int,
    val creditorProfile: Profile,
    val debtorProfile: Profile,
    val memoListResponses: List<MemoListResponse>,
    val repaymentHistories: List<RepaymentHistory>
)

data class PaperFormInfo(
    val primeAmount: Int,
    val interest: Int,
    val amount: Int,
    val remainingAmount: Int,
    val transactionDate: String,
    val repaymentStartDate: String,
    val repaymentEndDate: String,
    val specialConditions: String,
    val interestRate: Float,
    val interestPaymentDate: Int
)

data class Profile(
    val name: String,
    val phoneNumber: String,
    val address: String
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
