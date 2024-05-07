package com.alltimeowl.payrit.data.model

data class WritePromiseRequest(
    val amount: Int,
    val promiseStartDate: String,
    val promiseEndDate: String,
    val contents: String,
    val writerName: String,
    val participantsName: String,
    val participantsPhone: String,
    val promiseImageType: String
)
