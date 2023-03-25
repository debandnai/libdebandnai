package com.salonsolution.app.data.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.DashboardRepository
import com.salonsolution.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    var isMainMenu = MutableLiveData(true) // main menu or profile menu
    var isTopLevelDestination =
        MutableLiveData(true) // show back button if not top level destination
    var isToolbarMenuShow = MutableLiveData(true) // toolbar menu show or hide
    var isShowBottomNav = MutableLiveData(true) // bottom navigation menu show or hide
    var isShowToolbar = MutableLiveData(true) // toolbar show or hide
    var isCartPage = MutableLiveData(true) // cart total show or hide
    var toolbarTitle = MutableLiveData("")
    var userProfileImg = MutableLiveData("")
    var userProfileName = MutableLiveData("")
    var userFullName = MutableLiveData("")
    var cartTotalValue = MutableLiveData("")
    var notificationCount = MutableLiveData("")
    var cartCount = MutableLiveData(0)
    var isCurrencyChanged = MutableLiveData(false)

    val profileDetailsLiveData = userRepository.profileDetailsLiveData
    val logoutLiveData = userRepository.logoutLiveData
    val profileDeleteLiveData = userRepository.profileDeleteLiveData
    val notificationCountLiveData = userRepository.notificationCountLiveData
    val cartCountLiveData = cartRepository.cartCountLiveData

    fun getProfileDetails(profileDetailsRequest: JsonObject) {
        viewModelScope.launch {
            userRepository.profileDetails(profileDetailsRequest)
        }
    }

    fun logout(logoutRequest: JsonObject) {
        viewModelScope.launch {
            userRepository.logout(logoutRequest)
        }
    }


    fun profileDelete(logoutRequest: JsonObject) {
        viewModelScope.launch {
            userRepository.profileDelete(logoutRequest)
        }
    }

    fun notificationCount(notificationCountRequest: JsonObject) {
        viewModelScope.launch {
            userRepository.notificationCount(notificationCountRequest)
        }
    }

    fun cartCount(cartCountRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.cartCount(cartCountRequest)
        }
    }

}