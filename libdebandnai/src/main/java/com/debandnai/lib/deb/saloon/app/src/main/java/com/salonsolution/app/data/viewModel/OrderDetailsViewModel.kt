package com.salonsolution.app.data.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CartListModel
import com.salonsolution.app.data.model.OrderDetailsModel
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(private val orderRepository: OrderRepository) :
    ViewModel() {

    var orderId:Int?= null
    var orderDetailsResponseLiveData = orderRepository.orderDetailsResponseLiveData
    var cancelOrderResponseLiveData = orderRepository.cancelOrderResponseLiveData
    var buyAgainResponseLiveData = orderRepository.buyAgainResponseLiveData
    var reviewAddResponseLiveData = orderRepository.reviewAddResponseLiveData
    var orderDetailsModel: MutableLiveData<OrderDetailsModel> = MutableLiveData(OrderDetailsModel())
    var isCancelable= MutableLiveData(Pair(false,""))
    var isShowData= MutableLiveData(false)

    fun orderDetails(orderDetailsRequest: JsonObject) {
        viewModelScope.launch {
            orderRepository.orderDetails(orderDetailsRequest)
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

    fun reviewAdd(reviewAddRequest: JsonObject) {
        viewModelScope.launch {
            orderRepository.reviewAdd(reviewAddRequest)
        }
    }


    fun clearUpdateState() {
        orderRepository.clearUpdateState()
    }

}