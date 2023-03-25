package com.salonsolution.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.StaffList
import com.salonsolution.app.data.repository.StaffRepository
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffListViewModel @Inject constructor(private val repository: StaffRepository) :
    ViewModel() {

    var serviceId:Int = -1
    var serviceTime:String = ""
    var staffListLiveData: LiveData<PagingData<StaffList>> = MutableLiveData()
    val totalStaffCount: LiveData<String> = repository.totalStaffCountLiveData
    val anyStaffId: LiveData<Int> = repository.anyStaffIdLiveData
//    "sortField": "name",
//    "sortOrder": 1/-1, //1 -> ASC, -1 -> DESC
    val sortBy: MutableLiveData<Pair<String,Int>> = MutableLiveData(Pair(Constants.NAME,1))

    fun getStaffList(staffListRequest: JsonObject) {
        repository.staffListRequest.value = staffListRequest
        staffListLiveData = repository.staffList().cachedIn(viewModelScope)
    }



}