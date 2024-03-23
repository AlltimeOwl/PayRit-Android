package com.alltimeowl.payrit.ui.approval

import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.network.repository.IouRepository

class RecipientApprovalViewModel : ViewModel() {

    private val iouRepository = IouRepository()

    fun approvalIou(accessToken: String, paperId: Int) {
        iouRepository.approvalIou(accessToken, paperId)
    }
}