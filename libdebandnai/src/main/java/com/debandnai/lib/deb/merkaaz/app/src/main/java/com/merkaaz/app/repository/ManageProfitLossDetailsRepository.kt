package com.merkaaz.app.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject


class ManageProfitLossDetailsRepository @Inject constructor(private val retroApi: RetroApi) {
    // For Profit Loss Details List
    private val _profitLossDetailsList = MutableLiveData<Response<JsonobjectModel>>()
    public val profitLossDetailsList: LiveData<Response<JsonobjectModel>>
        get() = _profitLossDetailsList


    // For Profit Loss Save
    private val _profitLossSave = MutableLiveData<Response<JsonobjectModel>>()
    public val profitLossSave: LiveData<Response<JsonobjectModel>>
        get() = _profitLossSave


    // For Profit Loss Details List Response
    suspend fun getProfitLossDetailsListResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _profitLossDetailsList.postValue(Response.Loading())

            try {
                val res = retroApi.getProfitLossDetailsList(header, it)
                if (res.isSuccessful) {
                    _profitLossDetailsList.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _profitLossDetailsList.postValue(
                            Response.Error(
                                MethodClass.get_error_method(
                                    err
                                ), res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _profitLossDetailsList.postValue(Response.Error(e.toString(), -1))
            }

        }

    }






    // For Profit Loss Save Response
    suspend fun getProfitLossSaveResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _profitLossSave.postValue(Response.Loading())

            try {
                val res = retroApi.updateProfitLossData(header, it)
                if (res.isSuccessful) {
                    _profitLossSave.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _profitLossSave.postValue(
                            Response.Error(
                                MethodClass.get_error_method(
                                    err
                                ), res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _profitLossSave.postValue(Response.Error(e.toString(), -1))
            }

        }

    }

}