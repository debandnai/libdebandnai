package com.salonsolution.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.ReviewList
import com.salonsolution.app.data.model.ServiceImages
import com.salonsolution.app.data.model.StaffImages
import com.salonsolution.app.data.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffDetailsViewModel @Inject constructor(private val repository: StaffRepository) :
    ViewModel() {


    var staffId:Int = -1
    var serviceId:Int = -1
    var serviceTime:String = ""
    var staffName : String = ""
    var images:  ArrayList<StaffImages> = arrayListOf()
    var staffDetailsResponseLiveData = repository.staffDetailsResponseLiveData
    var staffReviewsLiveData: LiveData<PagingData<ReviewList>> = MutableLiveData()

    fun staffDetails(staffDetailsRequest: JsonObject) {
        viewModelScope.launch {
            repository.staffDetails(staffDetailsRequest)
        }
    }

    fun staffReviews(staffReviewsRequest: JsonObject) {
        repository.staffReviewsRequest.value = staffReviewsRequest
        staffReviewsLiveData = repository.staffReviewList().cachedIn(viewModelScope)
    }

}