package com.salonsolution.app.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    val cmsDetailsLiveData = userRepository.cmsDetailsLiveData

    fun cmsDetails(cmsDetailsRequest: JsonObject) {
        viewModelScope.launch {
            userRepository.cmsDetails(cmsDetailsRequest)
        }
    }

}