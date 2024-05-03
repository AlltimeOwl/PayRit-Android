package com.alltimeowl.payrit.ui.approval

import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.ModifyRequest
import com.alltimeowl.payrit.data.network.repository.IouRepository
import okhttp3.MultipartBody

class RecipientApprovalViewModel : ViewModel() {

    private val iouRepository = IouRepository()

    fun approvalIou(accessToken: String, paperId: Int, file: MultipartBody.Part) {
        iouRepository.approvalIou(accessToken, paperId, file)
    }

    fun modifyIouRequest(
        accessToken: String,
        modifyRequest: ModifyRequest,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Boolean) -> Unit
    ) {
        iouRepository.modifyIouRequest(accessToken, modifyRequest, onSuccess, onFailure)
    }

    fun modifyAcceptIou(accessToken: String, iouWriteRequest: IouWriteRequest, id:Int) {
        iouRepository.modifyAcceptIou(accessToken, iouWriteRequest, id)
    }
}