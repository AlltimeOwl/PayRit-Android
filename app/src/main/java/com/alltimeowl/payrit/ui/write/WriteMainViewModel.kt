package com.alltimeowl.payrit.ui.write

import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.UserCertificationResponse
import com.alltimeowl.payrit.data.network.repository.certificationRepository

class WriteMainViewModel : ViewModel() {

    private val certificationRepository = certificationRepository()

    fun checkCertification(
        accessToken: String,
        onSuccess: (Int) -> Unit,
        onFailure: (Int?) -> Unit
    ) {
        certificationRepository.checkCertification(accessToken, onSuccess, onFailure)
    }

    fun userCertification(accessToken: String, userCertificationResponse: UserCertificationResponse) {
        certificationRepository.userCertification(accessToken, userCertificationResponse)
    }
}
