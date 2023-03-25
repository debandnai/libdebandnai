package com.salonsolution.app.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CartListModel
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) :
    ViewModel() {

    var foodUpdateResponseLiveData = cartRepository.foodUpdateResponseLiveData
    var cartListResponseLiveData = cartRepository.cartListResponseLiveData
    var cartDeleteResponseLiveData = cartRepository.cartDeleteResponseLiveData
    var checkCartResponseLiveData = orderRepository.checkCartResponseLiveData
    var cartData :  CartListModel? = null

    fun foodUpdate(foodUpdateRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.foodUpdate(foodUpdateRequest)
        }
    }

    fun cartList(cartListRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.cartList(cartListRequest)
        }
    }

    fun cartDelete(cartDeleteRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.cartDelete(cartDeleteRequest)
        }
    }

    fun checkCart(checkCartRequest: JsonObject) {
        viewModelScope.launch {
            orderRepository.checkCart(checkCartRequest)
        }
    }

    fun clearUpdateState() {
        orderRepository.clearUpdateState()
    }

}