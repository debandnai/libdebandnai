package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.StaffCalendarModel
import com.salonsolution.app.data.model.StaffDetailsModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.paging.StaffListPagingSource
import com.salonsolution.app.data.paging.StaffReviewsPagingSource
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon.clear
import javax.inject.Inject

class StaffRepository @Inject constructor(private val userApi: UserApi, private val userSharedPref: UserSharedPref) {

    private val _staffDetailsResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<StaffDetailsModel>>>()
    val staffDetailsResponseLiveData: LiveData<ResponseState<BaseResponse<StaffDetailsModel>>>
        get() = _staffDetailsResponseLiveData

    private val _totalStaffCountLiveData =
        MutableLiveData<String>()
    val totalStaffCountLiveData: LiveData<String>
        get() = _totalStaffCountLiveData

    private val _anyStaffIdLiveData =
        MutableLiveData<Int>()
    val anyStaffIdLiveData: LiveData<Int>
        get() = _anyStaffIdLiveData

    private val _staffCalendarResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<StaffCalendarModel>>>()
    val staffCalendarResponseLiveData: LiveData<ResponseState<BaseResponse<StaffCalendarModel>>>
        get() = _staffCalendarResponseLiveData

    val staffListRequest = MutableLiveData<JsonObject>()
    fun staffList() = staffListRequest.switchMap { query ->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { StaffListPagingSource(userApi, query, _totalStaffCountLiveData, _anyStaffIdLiveData) }
        ).liveData
    }

    suspend fun staffDetails(staffDetailsRequest: JsonObject) {
        _staffDetailsResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.staffDetails(staffDetailsRequest)
            _staffDetailsResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _staffDetailsResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    val staffReviewsRequest = MutableLiveData<JsonObject>()
    fun staffReviewList() = staffReviewsRequest.switchMap { query ->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { StaffReviewsPagingSource(userApi, query) }
        ).liveData
    }


    suspend fun staffCalendar(staffCalendarRequest: JsonObject) {
        _staffCalendarResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.staffCalendar(staffCalendarRequest)
            _staffCalendarResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _staffCalendarResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    fun clearUpdateState() {
        _staffCalendarResponseLiveData.clear()

    }

}