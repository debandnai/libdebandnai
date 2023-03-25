package com.salonsolution.app.data.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.*
import com.salonsolution.app.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    var upcomingOrdersList: ArrayList<UpcomingOrderList> = arrayListOf()
    var recentOrdersList: ArrayList<UpcomingOrderList> = arrayListOf()
    var bookedServicesList: ArrayList<BookedServiceList> = arrayListOf()
    var recentServicesList: ArrayList<BookedServiceList> = arrayListOf()
    var isShowOrdersLayout = MutableLiveData(false)
    var isShowServiceLayout = MutableLiveData(false)
    var orderTabPosition: Int = 0
    var serviceTabPosition: Int = 0

    val allDataResponseLiveData = dashboardRepository.allDataResponseLiveData
    val upcomingOrdersResponseLiveData = dashboardRepository.upcomingOrdersResponseLiveData
    val recentOrdersResponseLiveData = dashboardRepository.recentOrdersResponseLiveData
    val bookedServicesResponseLiveData = dashboardRepository.bookedServicesResponseLiveData
    val recentServicesResponseLiveData = dashboardRepository.recentServicesResponseLiveData
    val popularServicesResponseLiveData = dashboardRepository.popularServicesResponseLiveData
    val latestReviewsResponseLiveData = dashboardRepository.latestReviewsResponseLiveData


    fun getAllData(allDataRequest: JsonObject) {
        viewModelScope.launch {
            dashboardRepository.getAllData(allDataRequest)
        }
    }

}