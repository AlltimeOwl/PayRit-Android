package com.alltimeowl.payrit.ui.promise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.data.model.WritePromiseRequest
import com.alltimeowl.payrit.data.network.repository.PromiseRepository

class PromiseViewModel : ViewModel() {

    private val promiseRepository = PromiseRepository()

    private val _promiseList = MutableLiveData<MutableList<PromiseData>>(mutableListOf())
    val promiseList: LiveData<MutableList<PromiseData>> = _promiseList

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

}