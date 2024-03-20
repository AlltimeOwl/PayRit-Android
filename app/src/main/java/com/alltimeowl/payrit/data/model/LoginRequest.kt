package com.alltimeowl.payrit.data.model

data class LoginRequest(
    val accessToken : String,
    val refreshToken: String
)

