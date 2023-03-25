package com.merkaaz.app.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject


class OrderDetailsRepository @Inject constructor(private val api : RetroApi) {

    // For Reorder
    private val _reorder = MutableLiveData<Response<JsonobjectModel>>()
    public val reOrder: LiveData<Response<JsonobjectModel>>
        get() = _reorder

    // For Reorder Response
    suspend fun getReorderResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            Log.d("response header", it.toString())
            _reorder.postValue(Response.Loading())

            try {
                val res = api.getReorder(header, it)
                if (res.isSuccessful) {
                    Log.d("response body", res.body().toString())
                    _reorder.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _reorder.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("error", e.toString())
                _reorder.postValue(Response.Error(e.toString(), -1))
            }

        }

    }
}