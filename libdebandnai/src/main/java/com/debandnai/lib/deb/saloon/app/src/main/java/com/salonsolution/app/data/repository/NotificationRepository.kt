package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.NotificationListModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.UtilsCommon.clear
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val userApi: UserApi, private val userSharedPref: UserSharedPref) {

    private val _notificationListResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<NotificationListModel>>>()
    val notificationListResponseLiveData: LiveData<ResponseState<BaseResponse<NotificationListModel>>>
        get() = _notificationListResponseLiveData

    private val _notificationDeleteResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val notificationDeleteResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _notificationDeleteResponseLiveData



    suspend fun notificationList(notificationListRequest: JsonObject) {
        _notificationListResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.notificationList(notificationListRequest)
            _notificationListResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _notificationListResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun notificationDelete(notificationDeleteRequest: JsonObject) {
        _notificationDeleteResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.notificationDelete(notificationDeleteRequest)
            _notificationDeleteResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _notificationDeleteResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }




    fun clearUpdateState() {
        _notificationListResponseLiveData.clear()
        _notificationDeleteResponseLiveData.clear()
    }

}