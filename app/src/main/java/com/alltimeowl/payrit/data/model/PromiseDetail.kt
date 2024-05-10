package com.alltimeowl.payrit.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PromiseDetail(
    val promiseId: Int,
    val amount: Long,
    val promiseStartDate: String,
    val promiseEndDate: String,
    val writerName: String,
    val contents: String,
    val participants: List<ParticipantsInfo>,
    val promiseImageType: String
) : Parcelable

@Parcelize
data class ParticipantsInfo(
    val participantsName: String,
    val participantsPhone: String
) : Parcelable
