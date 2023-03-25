package com.merkaaz.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject


class DashBoardRepository @Inject constructor(private val api: RetroApi) {
    // For user details
    private val _userDetails = MutableLiveData<Response<JsonobjectModel>>()
    val userDetails : LiveData<Response<JsonobjectModel>>
        get() = _userDetails

    private val _logOutResponse = MutableLiveData<Response<JsonobjectModel>>()
    val logOutResponse: LiveData<Response<JsonobjectModel>>
        get() = _logOutResponse


    // For userDetails Response
    suspend fun getUserDetailsResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _userDetails.postValue(Response.Loading())

            try {
                val res = api.getUserDetail(header,it)
                if (res.isSuccessful) {
                    _userDetails.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _userDetails.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                _userDetails.postValue(Response.Error(e.toString(),-1))
            }

        }

    }

    suspend fun logout(
        header: HashMap<String, String>,
        jsonObject: JsonObject?
    ) {
        jsonObject?.let {
            _logOutResponse.postValue(Response.Loading())
            try {
                val res = api.logout(header, it)
                if (res.isSuccessful) {
                    _logOutResponse.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _logOutResponse.postValue(
                            Response.Error(
                                MethodClass.get_error_method(
                                    err
                                ), res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _logOutResponse.postValue(
                    Response.Error(
                        e.toString(), -1
                    )
                )

            }
        }
    }
}