package com.alltimeowl.payrit.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.GetIouDetailResponse
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.data.network.repository.IouRepository

class HomeViewModel : ViewModel() {

    private val iouRepository = IouRepository()

    private val _iouList = MutableLiveData<List<getMyIouListResponse>>()
    val iouList: LiveData<List<getMyIouListResponse>> = _iouList

    private val _iouDetail = MutableLiveData<GetIouDetailResponse>()
    val iouDetail: LiveData<GetIouDetailResponse> = _iouDetail

    fun loadMyIouList(accessToken: String) {
        iouRepository.getMyIouList(accessToken) { iouList ->
            _iouList.value = iouList
        }
    }

    fun getIouDetail(accessToken: String, paperId: Int) {
        iouRepository.getIouDetail(accessToken, paperId) { response ->
            _iouDetail.postValue(response)
        }
    }
}