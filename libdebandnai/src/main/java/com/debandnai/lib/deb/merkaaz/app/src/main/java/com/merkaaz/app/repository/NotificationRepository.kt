package com.merkaaz.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val retroApi: RetroApi) {
    //notification list
    private val _notificationListResponse = MutableLiveData<Response<JsonobjectModel>>()
    val notificationListResponse: LiveData<Response<JsonobjectModel>>
        get() = _notificationListResponse

    //notification list
    private val _notificationDeleteResponse = MutableLiveData<Response<JsonobjectModel>>()
    val notificationDeleteResponse: LiveData<Response<JsonobjectModel>>
        get() = _notificationDeleteResponse

    //notification list api
    suspend fun notificationList(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _notificationListResponse.postValue(Response.Loading())
            try {
                val res = retroApi.getNotificationList(header, jsonObject)
                if (res.isSuccessful)
                    _notificationListResponse.postValue(Response.Success(res.body()))
                else
                    res.errorBody()?.let { err ->
                        _notificationListResponse.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )

                    }

            } catch (e: Exception) {
                _notificationListResponse.postValue(
                    Response.Error(
                        e.toString(),
                        -1
                    )
                )
            }
        }

    }

    //notification delete api
    suspend fun notificationDelete(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _notificationDeleteResponse.postValue(Response.Loading())
            try {
                val res = retroApi.notificationDelete(header, jsonObject)
                if (res.isSuccessful)
                    _notificationDeleteResponse.postValue(Response.Success(res.body()))
                else
                    res.errorBody()?.let { err ->
                        _notificationDeleteResponse.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )

                    }

            } catch (e: Exception) {
                _notificationDeleteResponse.postValue(
                    Response.Error(
                        e.toString(),
                        -1
                    )
                )
            }
        }

    }

}