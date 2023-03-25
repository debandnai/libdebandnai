package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CartCountModel
import com.salonsolution.app.data.model.CartListModel
import com.salonsolution.app.data.model.CouponModel
import com.salonsolution.app.data.model.NotificationItemCountModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.UtilsCommon.clear
import javax.inject.Inject

class CartRepository @Inject constructor(private val userApi: UserApi, private val userSharedPref: UserSharedPref) {

    private val _staffAddToCartResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val staffAddToCartResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _staffAddToCartResponseLiveData

    private val _productAddResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val productAddResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _productAddResponseLiveData

    private val _foodAddResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val foodAddResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _foodAddResponseLiveData

    private val _foodUpdateResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val foodUpdateResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _foodUpdateResponseLiveData

    private val _cartListResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<CartListModel>>>()
    val cartListResponseLiveData: LiveData<ResponseState<BaseResponse<CartListModel>>>
        get() = _cartListResponseLiveData

    private val _cartDeleteResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val cartDeleteResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _cartDeleteResponseLiveData

    private val _doNotNeedResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val doNotNeedResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _doNotNeedResponseLiveData


    private val _matchCouponResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<CouponModel>>>()
    val matchCouponResponseLiveData: LiveData<ResponseState<BaseResponse<CouponModel>>>
        get() = _matchCouponResponseLiveData

    private val _cartCountLiveData =
        MutableLiveData<ResponseState<BaseResponse<CartCountModel>>>()
    val cartCountLiveData: LiveData<ResponseState<BaseResponse<CartCountModel>>>
        get() = _cartCountLiveData

    suspend fun productAdd(productAddRequest: JsonObject) {
        _productAddResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.productAdd(productAddRequest)
            _productAddResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _productAddResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun staffAddToCart(addToCartRequest: JsonObject) {
        _staffAddToCartResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.addToCart(addToCartRequest)
            _staffAddToCartResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _staffAddToCartResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun foodAdd(foodAddRequest: JsonObject) {
        _foodAddResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.foodAdd(foodAddRequest)
            _foodAddResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _foodAddResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun foodUpdate(foodUpdateRequest: JsonObject) {
        _foodUpdateResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.foodUpdate(foodUpdateRequest)
            _foodUpdateResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _foodUpdateResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun cartList(cartListRequest: JsonObject) {
        _cartListResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.cartList(cartListRequest)
            _cartListResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _cartListResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun cartDelete(cartDeleteRequest: JsonObject) {
        _cartDeleteResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.cartDelete(cartDeleteRequest)
            _cartDeleteResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _cartDeleteResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun doNotNeed(cartDeleteRequest: JsonObject) {
        _doNotNeedResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.doNotNeed(cartDeleteRequest)
            _doNotNeedResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _doNotNeedResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun matchCoupon(matchCouponRequest: JsonObject) {
        _matchCouponResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.matchCoupon(matchCouponRequest)
            _matchCouponResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _matchCouponResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun cartCount(cartCountRequest: JsonObject) {
        _cartCountLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.cartCount(cartCountRequest)
            _cartCountLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _cartCountLiveData.postValue(ResponseState.create(throwable))
        }

    }



    fun clearUpdateState() {
        _staffAddToCartResponseLiveData.clear()
        _doNotNeedResponseLiveData.clear()
        _cartDeleteResponseLiveData.clear()
        _productAddResponseLiveData.clear()
        _foodAddResponseLiveData.clear()

    }

}