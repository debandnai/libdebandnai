package ie.healthylunch.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.notificationListPagingModel.NotificationList
import ie.healthylunch.app.data.model.notificationSettingsModel.NotiFicationSettingsResponse
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionList
import ie.healthylunch.app.data.model.updateSettingsModel.NotificationSettingsUpdateResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.paging.NotificationPagingSource
import ie.healthylunch.app.paging.TransactionsPagingSource
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.TOTAL_NO_OF_ITEMS_PER_PAGE
import ie.healthylunch.app.utils.SingleLiveEvent

class NotificationRepository(private val apiInterface: ApiInterface) : BaseRepository() {
    /*suspend fun notificationListRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.notificationListApi(jsonObject, token, "application/json")
    }*/
    suspend fun notificationDeleteRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.deleteNotificationApi(jsonObject, token, "application/json")
    }
    suspend fun notificationSettingsRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.notificationSettingsApi(jsonObject, token)
    }

    suspend fun notificationSettingsUpdateRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.notificationSettingsUpdateApi(jsonObject, token, "application/json")
    }


    //For notificationCountIncrease
    suspend fun notificationCountIncrease(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.pushNotificationCountIncrease(jsonObject, token, "application/json")
    }


    //Notification settings list
    val _notificationSettingsListResponse = SingleLiveEvent<Resource<NotiFicationSettingsResponse>?>()
    val notificationSettingsListResponse : LiveData<Resource<NotiFicationSettingsResponse>?>
        get() = _notificationSettingsListResponse




    //Notification update
    val _notificationSettingUpdateResponse = SingleLiveEvent<Resource<NotificationSettingsUpdateResponse>?>()
    val notificationSettingUpdateResponse : LiveData<Resource<NotificationSettingsUpdateResponse>?>
        get() = _notificationSettingUpdateResponse





    val notificationJsonRequest = MutableLiveData<JsonObject?>()
    fun getData(token: String):
            LiveData<PagingData<NotificationList>> = notificationJsonRequest.switchMap { query->
        Pager(
            config = PagingConfig(pageSize = TOTAL_NO_OF_ITEMS_PER_PAGE),
            pagingSourceFactory = { NotificationPagingSource(apiInterface,query,token) }
        ).liveData
    }




}