package com.alltimeowl.payrit.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.data.network.repository.IouRepository

class IouViewModel : ViewModel() {

    private val iouRepository = IouRepository()

    private val _iouList = MutableLiveData<List<getMyIouListResponse>>()
    val iouList: LiveData<List<getMyIouListResponse>> = _iouList

    fun loadMyIouList(accessToken: String) {
        iouRepository.getMyIouList(accessToken) { iouList ->
            _iouList.value = iouList
        }
    }
}