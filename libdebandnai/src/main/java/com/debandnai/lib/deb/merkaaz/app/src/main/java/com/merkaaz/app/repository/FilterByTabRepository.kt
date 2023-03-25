package com.merkaaz.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject


class FilterByTabRepository @Inject constructor(private val api : RetroApi) {

    private val _filter_list_res = MutableLiveData<Response<JsonobjectModel>>()
    val filter_list_res : LiveData<Response<JsonobjectModel>>
        get() = _filter_list_res

    suspend fun getFilterListResponse(jsonObject: JsonObject, header: java.util.HashMap<String, String>) {
        jsonObject?.let {
            _filter_list_res.postValue(Response.Loading())
            try {
                val res = api.getFilterList(header,it)
                if (res.isSuccessful) {
                    _filter_list_res.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _filter_list_res.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                _filter_list_res.postValue(Response.Error(e.toString(),-1))
            }

        }
    }

}