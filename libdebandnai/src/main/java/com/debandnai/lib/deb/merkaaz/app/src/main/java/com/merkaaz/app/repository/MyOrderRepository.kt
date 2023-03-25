package com.merkaaz.app.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.OrderList
import com.merkaaz.app.network.RetroApi

import com.merkaaz.app.paging.OrderPagingSource
import com.merkaaz.app.utils.Constants
import javax.inject.Inject


class MyOrderRepository @Inject constructor(private val retroApi: RetroApi) {

   /* //For order list response
    private val _orderListResponse = MutableLiveData<Response<JsonobjectModel>>()
    val orderListResponse: LiveData<Response<JsonobjectModel>>
        get() = _orderListResponse


    //Order list response fun
    suspend fun orderList(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _orderListResponse.postValue(Response.Loading())
            try {
                val res = retroApi.getOrderList(header, jsonObject)
                if (res.isSuccessful)
                    _orderListResponse.postValue(Response.Success(res.body()))
                else
                    res.errorBody()?.let { err ->
                        _orderListResponse.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }

            } catch (e: Exception) {
                _orderListResponse.postValue(
                    Response.Error(
                        e.toString(),
                        -1
                    )
                )
            }
        }

    }
*/
   val _currentQuery = MutableLiveData<JsonObject?>()
   fun getData(header: HashMap<String, String>):
           LiveData<PagingData<OrderList>> = _currentQuery.switchMap { query->
       Pager(
           config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
           pagingSourceFactory = { OrderPagingSource(retroApi,query,header) }
       ).liveData
   }
}