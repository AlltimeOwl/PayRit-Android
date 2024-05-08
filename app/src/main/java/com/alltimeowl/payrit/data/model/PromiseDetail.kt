package com.alltimeowl.payrit.data.model

data class PromiseDetail(
    val promiseId: Int,
    val amount: Long,
    val promiseStartDate: String,
    val promiseEndDate: String,
    val writerName: String,
    val contents: String,
    val participants: List<ParticipantsInfo>,
    val promiseImageType: String
)

data class ParticipantsInfo(
    val participantsName: String,
    val participantsPhone: String
)
