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
class MyOrderViewModel @Inject constructor(private val orderRepository: OrderRepository) :
    ViewModel() {

    var orderListResponseLiveData = orderRepository.orderListResponseLiveData
    var cancelOrderResponseLiveData = orderRepository.cancelOrderResponseLiveData
    var buyAgainResponseLiveData = orderRepository.buyAgainResponseLiveData

    fun orderList(orderListRequest: JsonObject) {
        viewModelScope.launch {
            orderRepository.orderList(orderListRequest)
        }
    }

    fun orderCancel(orderCancelRequest: JsonObject) {
        viewModelScope.launch {
            orderRepository.orderCancel(orderCancelRequest)
        }
    }

    fun buyAgain(buyAgainRequest: JsonObject) {
        viewModelScope.launch {
            orderRepository.buyAgain(buyAgainRequest)
        }
    }


    fun clearUpdateState() {
        orderRepository.clearUpdateState()
    }

}