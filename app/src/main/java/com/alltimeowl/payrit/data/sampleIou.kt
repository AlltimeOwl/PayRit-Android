package com.alltimeowl.payrit.data

data class sampleIou(
    val period: String,
    val type: String,
    val amount: String,
    val repay: String,
    val name: String,
    val day: String,
    val state: String
)

val sampleDataList = mutableListOf(
    sampleIou(
        period = "2024. 02.26 ~ 2024. 03.26",
        type = "빌려준 돈",
        amount = "30,000",
        repay = "0",
        name = "홍길동",
        day = "D-29",
        state = "승인 대기중"
    ),
    sampleIou(
        period = "2024. 03.01 ~ 2024. 03.10",
        type = "빌린 돈",
        amount = "10,000",
        repay = "0",
        name = "이순신",
        day = "D-39",
        state = "결제 대기중"
    ),
    sampleIou(
        period = "2024. 03.10 ~ 2024. 03.19",
        type = "빌려준 돈",
        amount = "30,000",
        repay = "10,000",
        name = "세종대왕",
        day = "D-59",
        state = "차용증 작성 완료"
    ),
    sampleIou(
        period = "2024. 04.01 ~ 2024. 06.10",
        type = "빌린 돈",
        amount = "100,000",
        repay = "100,000",
        name = "장보고",
        day = "D-79",
        state = "기간 만료"
    ),
    sampleIou(
        period = "2024. 04.28 ~ 2025. 05.10",
        type = "빌려준 돈",
        amount = "10,000,000",
        repay = "6,000,000",
        name = "정주성",
        day = "D-129",
        state = "차용증 작성 완료"
    )
)