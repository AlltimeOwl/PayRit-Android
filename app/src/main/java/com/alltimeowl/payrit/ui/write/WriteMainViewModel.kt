package com.alltimeowl.payrit.ui.write

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.CertificationInfoResponse
import com.alltimeowl.payrit.data.model.UserCertificationResponse
import com.alltimeowl.payrit.data.network.repository.CertificationRepository

class WriteMainViewModel : ViewModel() {

    private val certificationRepository = CertificationRepository()

    private val _certificationUserInfo = MutableLiveData<CertificationInfoResponse>()
    val certificationUserInfo: LiveData<CertificationInfoResponse> = _certificationUserInfo

    fun checkCertification(
        accessToken: String,
        onSuccess: (Int) -> Unit,
        onFailure: (Int?) -> Unit
    ) {
        certificationRepository.checkCertification(accessToken, onSuccess, onFailure)
    }

    fun userCertification(
        accessToken: String,
        userCertificationResponse: UserCertificationResponse,
        onSuccess: (Int) -> Unit,
        onFailure: (Int?) -> Unit
        ) {
        certificationRepository.userCertification(accessToken, userCertificationResponse, onSuccess, onFailure)
    }

    fun getCertificationInfo(accessToken: String) {
        certificationRepository.getCertificationInfo(accessToken) { certificationInfoResponse ->
            _certificationUserInfo.value = certificationInfoResponse
        }
    }
}
