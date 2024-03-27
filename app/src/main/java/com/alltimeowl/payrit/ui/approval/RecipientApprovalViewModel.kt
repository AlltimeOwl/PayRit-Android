package com.alltimeowl.payrit.ui.approval

import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.network.repository.IouRepository
import okhttp3.MultipartBody

class RecipientApprovalViewModel : ViewModel() {

    private val iouRepository = IouRepository()

    fun approvalIou(accessToken: String, paperId: Int, file: MultipartBody.Part) {
        iouRepository.approvalIou(accessToken, paperId, file)
    }
}