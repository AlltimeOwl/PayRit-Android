package com.alltimeowl.payrit.ui.promise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.data.model.PromiseDetail
import com.alltimeowl.payrit.data.model.WritePromiseRequest
import com.alltimeowl.payrit.data.network.repository.PromiseRepository

class PromiseViewModel : ViewModel() {

    private val promiseRepository = PromiseRepository()

    private val _promiseList = MutableLiveData<MutableList<PromiseData>>(mutableListOf())
    val promiseList: LiveData<MutableList<PromiseData>> = _promiseList

    private val _myPromiseList = MutableLiveData<List<PromiseDetail>>()
    val myPromiseList: LiveData<List<PromiseDetail>> = _myPromiseList

    fun addPromiseData(data: PromiseData) {
        val currentList = _promiseList.value ?: mutableListOf()
        currentList.add(data)
        _promiseList.value = currentList
    }

    fun removePromiseData(position: Int) {
        val currentList = _promiseList.value ?: mutableListOf()
        if (position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            _promiseList.value = currentList
        }
    }

    fun writePromise(
        accessToken: String,
        writePromiseRequest: WritePromiseRequest,
        onSuccess: (Long) -> Unit,
        onFailure: (Boolean) -> Unit
    ) {
        promiseRepository.writePromise(accessToken, writePromiseRequest, onSuccess, onFailure)
    }

    fun getMyPromiseList(accessToken: String) {
        promiseRepository.getMyPromiseList(accessToken) { promiseList ->
            _myPromiseList.value = promiseList
        }
    }

    fun deletePromise(
        accessToken: String,
        id:Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Boolean) -> Unit
    ) {
        promiseRepository.deletePromise(accessToken, id, onSuccess, onFailure)
    }

    fun sharePromise(accessToken: String, id: Int, onSuccess: (Boolean) -> Unit) {
        promiseRepository.sharePromise(accessToken, id, onSuccess)
    }

}