package com.salonsolution.app.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(private val repository: NotificationRepository) :
    ViewModel() {

    var notificationListResponseLiveData = repository.notificationListResponseLiveData
    var notificationDeleteResponseLiveData = repository.notificationDeleteResponseLiveData


    fun notificationList(notificationListRequest: JsonObject) {
        viewModelScope.launch {
            repository.notificationList(notificationListRequest)
        }
    }

    fun notificationDelete(notificationDeleteRequest: JsonObject) {
        viewModelScope.launch {
            repository.notificationDelete(notificationDeleteRequest)
        }
    }


    fun clearUpdateState() {
        repository.clearUpdateState()
    }

}