package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.deletenotificationModel.DeleteNotificationResponse
import ie.healthylunch.app.data.model.notificationCountIncreaseModel.NotificationCountIncreaseResponse
import ie.healthylunch.app.data.model.notificationListModel.NotificationListResponse
import ie.healthylunch.app.data.model.notificationListPagingModel.NotificationList
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionList
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.NotificationRepository
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    var noDataTextVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var recyclerViewVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var clearAllTextViewVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    //Notification ist
    val _notificationListResponse: SingleLiveEvent<Resource<NotificationListResponse>?> =
        SingleLiveEvent()
    var notificationListResponse: LiveData<Resource<NotificationListResponse>?>? = null
        get() = _notificationListResponse

    /*fun notificationList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _notificationListResponse.value = repository.notificationListRepository(jsonObject, token)
    }*/

    //Notification Delete
    val _notificationDeleteResponse: MutableLiveData<Resource<DeleteNotificationResponse>?> =
        MutableLiveData()
    var notificationDeleteResponse: LiveData<Resource<DeleteNotificationResponse>?>? = null
        get() = _notificationDeleteResponse

    fun notificationDelete(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _notificationDeleteResponse.value =
            repository.notificationDeleteRepository(jsonObject, token)
    }

    // notification Count Increase
    val _notificationCountIncreaseResponse: SingleLiveEvent<Resource<NotificationCountIncreaseResponse>?> =
        SingleLiveEvent()
    var notificationCountIncreaseResponse: LiveData<Resource<NotificationCountIncreaseResponse>?>? =
        null
        get() = _notificationCountIncreaseResponse

    fun notificationCountIncrease(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _notificationCountIncreaseResponse.value =
            repository.notificationCountIncrease(jsonObject, token)
    }

    var data_list : LiveData<PagingData<NotificationList>> = MutableLiveData()
    fun getData(jsonBody: JsonObject,token: String) {
        repository.notificationJsonRequest.value = jsonBody
        data_list = repository.getData(token).cachedIn(viewModelScope)
    }
}