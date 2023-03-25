package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CategoriesModel
import com.salonsolution.app.data.model.ServiceDetailsModel
import com.salonsolution.app.data.model.ServicesListModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.paging.ServiceListPagingSource
import com.salonsolution.app.data.paging.ServiceSearchPagingSource
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.Constants
import javax.inject.Inject

class ServiceRepository @Inject constructor(private val userApi: UserApi, private val userSharedPref: UserSharedPref) {


    private val _categoriesResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<CategoriesModel>>>()
    val categoriesResponseLiveData: LiveData<ResponseState<BaseResponse<CategoriesModel>>>
        get() = _categoriesResponseLiveData

    private val _serviceDetailsResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<ServiceDetailsModel>>>()
    val serviceDetailsResponseLiveData: LiveData<ResponseState<BaseResponse<ServiceDetailsModel>>>
        get() = _serviceDetailsResponseLiveData

    private val _totalServicesCountLiveData =
        MutableLiveData<String>()
    val totalServicesCountLiveData: LiveData<String>
        get() = _totalServicesCountLiveData

    private val _totalServicesSearchCountLiveData =
        MutableLiveData("0")
    val totalServicesSearchCountLiveData: LiveData<String>
        get() = _totalServicesSearchCountLiveData

//    private val _serviceSearchResponseLiveData =
//        MutableLiveData<ResponseState<BaseResponse<ServicesListModel>>>()
//    val serviceSearchResponseLiveData: LiveData<ResponseState<BaseResponse<ServicesListModel>>>
//        get() = _serviceSearchResponseLiveData

    suspend fun allCategories(categoriesRequest: JsonObject) {
        _categoriesResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.allCategories(categoriesRequest)
            _categoriesResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _categoriesResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    val serviceListRequest = MutableLiveData<JsonObject>()
    fun serviceList() = serviceListRequest.switchMap { query ->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { ServiceListPagingSource(userApi, query, _totalServicesCountLiveData) }
        ).liveData
    }

    suspend fun serviceDetails(serviceDetailsRequest: JsonObject) {
        _serviceDetailsResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.serviceDetails(serviceDetailsRequest)
            _serviceDetailsResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _serviceDetailsResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    val serviceSearchRequest = MutableLiveData<JsonObject>()
    fun serviceSearchList() = serviceSearchRequest.switchMap { query ->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { ServiceSearchPagingSource(userApi, query, _totalServicesSearchCountLiveData) }
        ).liveData
    }

//    suspend fun serviceSearch(serviceSearchRequest: JsonObject) {
//        _serviceSearchResponseLiveData.postValue(ResponseState.Loading())
//        try {
//            val response = userApi.serviceSearch(serviceSearchRequest)
//            _serviceSearchResponseLiveData.postValue(ResponseState.create(response, userSharedPref))
//
//        } catch (throwable: Throwable) {
//            _serviceSearchResponseLiveData.postValue(ResponseState.create(throwable))
//        }
//
//    }

}