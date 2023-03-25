package com.merkaaz.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject


class MapRepository @Inject constructor(private val api : RetroApi) {

    // For Location Update
    private val _locationUpdate = MutableLiveData<Response<JsonobjectModel>>()
    public val locationUpdate: LiveData<Response<JsonobjectModel>>
        get() = _locationUpdate

    // For Location Update
    suspend fun locationUpdateResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _locationUpdate.postValue(Response.Loading())

            try {
                val res = api.getLocationUpdate(header, it)
                if (res.isSuccessful) {
                    // For Location Update
                    suspend fun locationUpdateResponse(
                        body: JsonObject?,
                        header: HashMap<String, String>
                    ) {
                        body?.let {
                            _locationUpdate.postValue(Response.Loading())

                            try {
                                val res = api.getLocationUpdate(header, it)
                                if (res.isSuccessful) {
                                    _locationUpdate.postValue(Response.Success(res.body()))
                                } else {
                                    res.errorBody()?.let { err ->
                                        _locationUpdate.postValue(
                                            Response.Error(
                                                MethodClass.get_error_method(
                                                    err
                                                ), res.code()
                                            )
                                        )
                                    }
                                }

                            } catch (e: Exception) {
                                _locationUpdate.postValue(Response.Error(e.toString(), -1))
                            }

                        }

                    }
                    _locationUpdate.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _locationUpdate.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _locationUpdate.postValue(Response.Error(e.toString(), -1))
            }

        }

    }
}