package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.ProductDetailsModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.paging.ProductListPagingSource
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.Constants
import javax.inject.Inject

class ProductRepository @Inject constructor(private val userApi: UserApi, private val userSharedPref: UserSharedPref) {

    private val _productDetailsResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<ProductDetailsModel>>>()
    val productDetailsResponseLiveData: LiveData<ResponseState<BaseResponse<ProductDetailsModel>>>
        get() = _productDetailsResponseLiveData

    private val _totalProductCountLiveData =
        MutableLiveData<String>()
    val totalProductCountLiveData: LiveData<String>
        get() = _totalProductCountLiveData

    val productListRequest = MutableLiveData<JsonObject>()
    fun productList() = productListRequest.switchMap { query ->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { ProductListPagingSource(userApi, query, _totalProductCountLiveData) }
        ).liveData
    }

    suspend fun productDetails(productDetailsRequest: JsonObject) {
        _productDetailsResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.productDetails(productDetailsRequest)
            _productDetailsResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _productDetailsResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

}