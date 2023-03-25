package com.merkaaz.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject

class CartRepository @Inject constructor(private val retroApi: RetroApi) {
    //for cart list
    private val _cartListResponse = MutableLiveData<Response<JsonobjectModel>>()
    val cartListResponse: LiveData<Response<JsonobjectModel>>
        get() = _cartListResponse

    //for cart product avialable
    private val _cartProductAvailabilityCheckResponse = MutableLiveData<Response<JsonobjectModel>>()
    val cartProductAvailabilityCheckResponse: LiveData<Response<JsonobjectModel>>
        get() = _cartProductAvailabilityCheckResponse


    //cart list response fun
    suspend fun cartList(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _cartListResponse.postValue(Response.Loading())
            try {
                val res = retroApi.getCartList(header, jsonObject)
                if (res.isSuccessful)
                    _cartListResponse.postValue(Response.Success(res.body()))
                else
                    res.errorBody()?.let { err ->
                        _cartListResponse.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }

            } catch (e: Exception) {
                _cartListResponse.postValue(
                    Response.Error(
                        e.toString(),
                        -1
                    )
                )
            }
        }

    }
    //cart Product Availability Check response fun
    suspend fun cartProductAvailabilityCheck(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _cartProductAvailabilityCheckResponse.postValue(Response.Loading())
            try {
                val res = retroApi.cartProductAvailabilityCheck(header, jsonObject)
                if (res.isSuccessful)
                    _cartProductAvailabilityCheckResponse.postValue(Response.Success(res.body()))
                else
                    res.errorBody()?.let { err ->
                        _cartProductAvailabilityCheckResponse.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }

            } catch (e: Exception) {
                _cartProductAvailabilityCheckResponse.postValue(
                    Response.Error(
                        e.toString(),
                        -1
                    )
                )
            }
        }

    }
}
