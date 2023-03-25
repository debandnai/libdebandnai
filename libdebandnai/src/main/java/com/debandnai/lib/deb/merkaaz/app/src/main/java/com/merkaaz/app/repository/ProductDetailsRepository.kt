package com.merkaaz.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject

class ProductDetailsRepository @Inject constructor(val api: RetroApi) {
    private val _productDetailsResponse = MutableLiveData<Response<JsonobjectModel>>()
    val productDetailsResponse: LiveData<Response<JsonobjectModel>>
        get() = _productDetailsResponse

    suspend fun getProductDetailsResponse(
        header: HashMap<String, String>,
        jsonObject: JsonObject?
    ) {
        jsonObject?.let {
            _productDetailsResponse.postValue(Response.Loading())

            try {
                val res = api.productDetail(header, it)
                if (res.isSuccessful) {
                    _productDetailsResponse.postValue(Response.Success(res.body()))

                } else {
                    res.errorBody()?.let { err ->
                        _productDetailsResponse.postValue(
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
                _productDetailsResponse.postValue(
                    Response.Error(
                        e.toString(), -1
                    )
                )

            }


        }

    }

}