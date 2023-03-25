package com.merkaaz.app.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject


class PaymentRepository  @Inject constructor(private val retroApi : RetroApi) {

    //for save order
    private val _saveOrderResponse = MutableLiveData<Response<JsonobjectModel>>()
    val saveResponse: LiveData<Response<JsonobjectModel>>
        get() = _saveOrderResponse

//    //for Payment Details
//    private val _PaymentDetailsResponse = MutableLiveData<Response<JsonobjectModel>>()
//    val PaymentDetailsResponse: LiveData<Response<JsonobjectModel>>
//        get() = _PaymentDetailsResponse

    //for move on price
    private val _MoveonPrice = MutableLiveData<Response<JsonobjectModel>>()
    val moveOnPriceLiveData: LiveData<Response<JsonobjectModel>>
        get() = _MoveonPrice

    //save order response fun
    suspend fun saveOrder(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _saveOrderResponse.postValue(Response.Loading())
            try {
                val res = retroApi.saveOrder(header, jsonObject)
                if (res.isSuccessful)
                    _saveOrderResponse.postValue(Response.Success(res.body()))
                else
                    res.errorBody()?.let { err ->
                        _saveOrderResponse.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }

            } catch (e: Exception) {
                _saveOrderResponse.postValue(
                    Response.Error(
                        e.toString(),
                        -1
                    )
                )
            }
        }
}




//    //Payment Details response fun
//    suspend fun PaymentDetails(jsonObject: JsonObject?, header: HashMap<String, String>) {
//        jsonObject?.let {
//            _PaymentDetailsResponse.postValue(Response.Loading())
//            try {
//                val res = retroApi.getPaymentDetails(header, jsonObject)
//                if (res.isSuccessful)
//                    _PaymentDetailsResponse.postValue(Response.Success(res.body()))
//                else
//                    res.errorBody()?.let { err ->
//                        _PaymentDetailsResponse.postValue(
//                            Response.Error(
//                                MethodClass.get_error_method(err),
//                                res.code()
//                            )
//                        )
//                    }
//
//            } catch (e: Exception) {
//                _PaymentDetailsResponse.postValue(
//                    Response.Error(
//                        e.toString(),
//                        -1
//                    )
//                )
//            }
//        }
//
//    }

    //check move on price
    suspend fun moveonPrice(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _MoveonPrice.postValue(Response.Loading())
            try {
                val res = retroApi.checkMoveonPrice(header, jsonObject)
                if (res.isSuccessful)
                    _MoveonPrice.postValue(Response.Success(res.body()))
                else
                    res.errorBody()?.let { err ->
                        _MoveonPrice.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }

            } catch (e: Exception) {
                _MoveonPrice.postValue(
                    Response.Error(
                        e.toString(),
                        -1
                    )
                )
            }
        }

    }

}