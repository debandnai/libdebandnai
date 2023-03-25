package com.merkaaz.app.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.utils.MethodClass
import java.lang.Exception
import javax.inject.Inject


class DeliveryOptionsRepository  @Inject constructor(private val api : RetroApi) {
    // For Delivery Options
    private val _deliveryOptions = MutableLiveData<Response<JsonobjectModel>>()
    public val deliveryOptions : LiveData<Response<JsonobjectModel>>
        get() = _deliveryOptions



    // For Delivery Options
    suspend fun getDeliveryOptionsResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _deliveryOptions.postValue(Response.Loading())

            try {
                val res = api.getDeliveryOptions(header,it)
                if (res.isSuccessful) {
                    _deliveryOptions.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _deliveryOptions.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                _deliveryOptions.postValue(Response.Error(e.toString(),-1))
            }

        }

    }

}