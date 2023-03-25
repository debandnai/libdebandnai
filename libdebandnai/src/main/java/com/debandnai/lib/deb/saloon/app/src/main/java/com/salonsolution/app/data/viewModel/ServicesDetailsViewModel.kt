package com.salonsolution.app.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.ServiceImages
import com.salonsolution.app.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesDetailsViewModel @Inject constructor(private val repository: ServiceRepository) :
    ViewModel() {


    var serviceId:Int = -1
    var serviceTime :String? = null
    var serviceDetailsResponseLiveData = repository.serviceDetailsResponseLiveData
    var images:  ArrayList<ServiceImages> = arrayListOf()

    fun serviceDetails(serviceDetailsRequest: JsonObject) {
        viewModelScope.launch {
            repository.serviceDetails(serviceDetailsRequest)
        }
    }

}