package com.alltimeowl.payrit.ui.mypage

import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.WithdrawalRequest
import com.alltimeowl.payrit.data.network.repository.LoginRepository

class MyPageViewModel: ViewModel() {

    private val loginRepository = LoginRepository()

    fun logoutUser(accessToken: String) {
        loginRepository.logoutUser(accessToken)
    }

    fun withdrawalUser(accessToken: String, withdrawalRequest: WithdrawalRequest) {
        loginRepository.withdrawalUser(accessToken, withdrawalRequest)
    }

}