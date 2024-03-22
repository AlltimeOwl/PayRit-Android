package com.alltimeowl.payrit.data.model

data class IouWriteResponse(
    val success: Boolean,
    val status: Int,
    val code: String,
    val reason: String,
    val timeStamp: String,
    val path: String,
)
