package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.BuyAgainModel
import com.salonsolution.app.data.model.CheckCartModel
import com.salonsolution.app.data.model.OrderDetailsModel
import com.salonsolution.app.data.model.OrderListModel
import com.salonsolution.app.data.model.OrderSaveModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.UtilsCommon.clear
import javax.inject.Inject

class OrderRepository @Inject constructor(private val userApi: UserApi, private val userSharedPref: UserSharedPref) {

    private val _checkCartResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<CheckCartModel>>>()
    val checkCartResponseLiveData: LiveData<ResponseState<BaseResponse<CheckCartModel>>>
        get() = _checkCartResponseLiveData

    private val _orderSaveResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<OrderSaveModel>>>()
    val orderSaveResponseLiveData: LiveData<ResponseState<BaseResponse<OrderSaveModel>>>
        get() = _orderSaveResponseLiveData

    private val _orderListResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<OrderListModel>>>()
    val orderListResponseLiveData: LiveData<ResponseState<BaseResponse<OrderListModel>>>
        get() = _orderListResponseLiveData

    private val _cancelOrderResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val cancelOrderResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _cancelOrderResponseLiveData

    private val _buyAgainResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<BuyAgainModel>>>()
    val buyAgainResponseLiveData: LiveData<ResponseState<BaseResponse<BuyAgainModel>>>
        get() = _buyAgainResponseLiveData

    private val _orderDetailsResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<OrderDetailsModel>>>()
    val orderDetailsResponseLiveData: LiveData<ResponseState<BaseResponse<OrderDetailsModel>>>
        get() = _orderDetailsResponseLiveData

    private val _reviewAddResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val reviewAddResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _reviewAddResponseLiveData


    suspend fun checkCart(checkCartRequest: JsonObject) {
        _checkCartResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.checkCart(checkCartRequest)
            _checkCartResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _checkCartResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun orderSave(orderSaveRequest: JsonObject) {
        _orderSaveResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.orderSave(orderSaveRequest)
            _orderSaveResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _orderSaveResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun orderList(orderListRequest: JsonObject) {
        _orderListResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.orderList(orderListRequest)
            _orderListResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _orderListResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun orderCancel(orderCancelRequest: JsonObject) {
        _cancelOrderResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.orderCancel(orderCancelRequest)
            _cancelOrderResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _cancelOrderResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun buyAgain(buyAgainRequest: JsonObject) {
        _buyAgainResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.buyAgain(buyAgainRequest)
            _buyAgainResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _buyAgainResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun orderDetails(orderDetailsRequest: JsonObject) {
        _orderDetailsResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.orderDetails(orderDetailsRequest)
            _orderDetailsResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _orderDetailsResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun reviewAdd(reviewAddRequest: JsonObject) {
        _reviewAddResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.reviewAdd(reviewAddRequest)
            _reviewAddResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _reviewAddResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }



    fun clearUpdateState() {
        _checkCartResponseLiveData.clear()
        _orderSaveResponseLiveData.clear()
        _buyAgainResponseLiveData.clear()
        _cancelOrderResponseLiveData.clear()
        _reviewAddResponseLiveData.clear()


    }

}