package com.alltimeowl.payrit.ui.write

import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.network.repository.IouWriteRepository

class IouWriteViewModel: ViewModel() {

    private val louWriteRepository = IouWriteRepository()

    fun iouWrite(accessToken: String, iouWriteRequest: IouWriteRequest) {
        louWriteRepository.iouWrite(accessToken, iouWriteRequest)
    }

}