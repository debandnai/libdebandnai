package com.salonsolution.app.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.salonsolution.app.data.model.ServicesList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val requestBodyHelper: RequestBodyHelper
) : ViewModel() {

    var searchData= MutableLiveData("")
    var totalServicesCount= serviceRepository.totalServicesSearchCountLiveData
//    var serviceSearchResponseLiveData = serviceRepository.serviceSearchResponseLiveData
    var serviceSearchListLiveData: LiveData<PagingData<ServicesList>> = MutableLiveData()

    init {
        serviceSearch("")
    }

    private fun serviceSearch(searchText:String?) {
        serviceRepository.serviceSearchRequest.value = requestBodyHelper.getServiceSearchRequest(searchText)
        serviceSearchListLiveData = serviceRepository.serviceSearchList().cachedIn(viewModelScope)
    }

    private var searchJob: Job? = null
    fun searchDebounced(searchText: String?) {
        Log.d("searchDebounced", "Debounced call")
        searchJob?.cancel()
        if (searchText.isNullOrEmpty() || searchText.length <= 2)
            return
        searchJob = viewModelScope.launch {
            delay(600)
            serviceSearch(searchText.trim())
            Log.d("searchDebounced", "search api call: $searchText")
        }
    }


}