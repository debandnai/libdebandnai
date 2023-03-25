package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.*
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.preferences.UserSharedPref
import kotlinx.coroutines.*
import retrofit2.Response
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val userApi: UserApi,
    private val userSharedPref: UserSharedPref
) {

    private val _allDataResponseLiveData =
        MutableLiveData<ResponseState<String>>()
    val allDataResponseLiveData: LiveData<ResponseState<String>>
        get() = _allDataResponseLiveData

    private val _upcomingOrdersResponseLiveData =
        MutableLiveData<BaseResponse<UpcomingOrderListModel>>()
    val upcomingOrdersResponseLiveData: LiveData<BaseResponse<UpcomingOrderListModel>>
        get() = _upcomingOrdersResponseLiveData

    private val _recentOrdersResponseLiveData =
        MutableLiveData<BaseResponse<RecentOrderListModel>>()
    val recentOrdersResponseLiveData: LiveData<BaseResponse<RecentOrderListModel>>
        get() = _recentOrdersResponseLiveData

    private val _bookedServicesResponseLiveData =
        MutableLiveData<BaseResponse<BookedServiceListModel>>()
    val bookedServicesResponseLiveData: LiveData<BaseResponse<BookedServiceListModel>>
        get() = _bookedServicesResponseLiveData

    private val _recentServicesResponseLiveData =
        MutableLiveData<BaseResponse<RecentServiceListModel>>()
    val recentServicesResponseLiveData: LiveData<BaseResponse<RecentServiceListModel>>
        get() = _recentServicesResponseLiveData

    private val _popularServicesResponseLiveData =
        MutableLiveData<BaseResponse<PopularCategoryListModel>>()
    val popularServicesResponseLiveData: LiveData<BaseResponse<PopularCategoryListModel>>
        get() = _popularServicesResponseLiveData

    private val _latestReviewsResponseLiveData =
        MutableLiveData<BaseResponse<LatestReviewListModel>>()
    val latestReviewsResponseLiveData: LiveData<BaseResponse<LatestReviewListModel>>
        get() = _latestReviewsResponseLiveData



    suspend fun getAllData(
        allRequest: JsonObject
    ) {
        withContext(Dispatchers.IO) {
            _allDataResponseLiveData.postValue(ResponseState.Loading())

            //supervisorScope for catch the exception from await()
            val call1 = supervisorScope { async { userApi.upcomingOrders(allRequest) } }
            val call2 = supervisorScope { async { userApi.recentOrders(allRequest) } }
            val call3 = supervisorScope { async { userApi.bookedServices(allRequest) } }
            val call4 = supervisorScope { async { userApi.recentServices(allRequest) } }
            val call5 = supervisorScope { async { userApi.popularServices(allRequest) } }
            val call6 = supervisorScope { async { userApi.latestReviews(allRequest) } }

            try {
                val upcomingOrdersResponse = call1.await()
                val recentOrdersResponse = call2.await()
                val bookedServicesResponse = call3.await()
                val recentServicesResponse = call4.await()
                val popularServicesResponse = call5.await()
                val latestReviewsResponse = call6.await()
                if (upcomingOrdersResponse.isSuccessful
                    && recentOrdersResponse.isSuccessful
                    && bookedServicesResponse.isSuccessful
                    && recentServicesResponse.isSuccessful
                    && popularServicesResponse.isSuccessful
                    && latestReviewsResponse.isSuccessful
                ) {

                    _upcomingOrdersResponseLiveData.postValue(upcomingOrdersResponse.body())
                    _recentOrdersResponseLiveData.postValue(recentOrdersResponse.body())
                    _bookedServicesResponseLiveData.postValue(bookedServicesResponse.body())
                    _recentServicesResponseLiveData.postValue(recentServicesResponse.body())
                    _popularServicesResponseLiveData.postValue(popularServicesResponse.body())
                    _latestReviewsResponseLiveData.postValue(latestReviewsResponse.body())

                    _allDataResponseLiveData.postValue(ResponseState.Success("Successful"))

                } else if (!upcomingOrdersResponse.isSuccessful) {

                    val errorResponse = ResponseState.create(upcomingOrdersResponse, userSharedPref)
                    _allDataResponseLiveData.postValue(
                        ResponseState.Error(
                            false,
                            errorResponse.errorMessage,
                            errorResponse.errorCode
                        )
                    )
                } else if (!recentOrdersResponse.isSuccessful) {

                    val errorResponse = ResponseState.create(recentOrdersResponse, userSharedPref)
                    _allDataResponseLiveData.postValue(
                        ResponseState.Error(
                            false,
                            errorResponse.errorMessage,
                            errorResponse.errorCode
                        )
                    )
                } else if (!bookedServicesResponse.isSuccessful) {
                    val errorResponse = ResponseState.create(bookedServicesResponse, userSharedPref)
                    _allDataResponseLiveData.postValue(
                        ResponseState.Error(
                            false,
                            errorResponse.errorMessage,
                            errorResponse.errorCode
                        )
                    )
                } else if (!recentServicesResponse.isSuccessful) {
                    val errorResponse = ResponseState.create(recentServicesResponse, userSharedPref)
                    _allDataResponseLiveData.postValue(
                        ResponseState.Error(
                            false,
                            errorResponse.errorMessage,
                            errorResponse.errorCode
                        )
                    )
                }
                else if (!popularServicesResponse.isSuccessful) {
                    val errorResponse = ResponseState.create(popularServicesResponse, userSharedPref)
                    _allDataResponseLiveData.postValue(
                        ResponseState.Error(
                            false,
                            errorResponse.errorMessage,
                            errorResponse.errorCode
                        )
                    )
                }
                else if (!latestReviewsResponse.isSuccessful) {
                    val errorResponse = ResponseState.create(latestReviewsResponse, userSharedPref)
                    _allDataResponseLiveData.postValue(
                        ResponseState.Error(
                            false,
                            errorResponse.errorMessage,
                            errorResponse.errorCode
                        )
                    )
                }else {
                    _allDataResponseLiveData.postValue(
                        ResponseState.Error(
                            false,
                            null,
                            null
                        )
                    )
                }
            } catch (throwable: Throwable) {
                _allDataResponseLiveData.postValue(ResponseState.create(throwable))

            }
        }
    }
}