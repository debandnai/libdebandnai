package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.OrderDetailsRepository
import com.merkaaz.app.utils.CommonFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderDetailsRepository: OrderDetailsRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {

    var waitingForPickup: MutableLiveData<String> = MutableLiveData("")
    var orderId: MutableLiveData<String> = MutableLiveData("")
    var id: MutableLiveData<String> = MutableLiveData("")
    var orderDate: MutableLiveData<String> = MutableLiveData("")
    var orderValue: MutableLiveData<String> = MutableLiveData("")
    var deliveryCharge: MutableLiveData<String> = MutableLiveData("")
    var totalAmt: MutableLiveData<String> = MutableLiveData("")
    var total: MutableLiveData<String> = MutableLiveData("")
    var deliveryAddress: MutableLiveData<String> = MutableLiveData("")
    var shippingType: MutableLiveData<String> = MutableLiveData("")
    var paymentStatus: MutableLiveData<String> = MutableLiveData("")
    var paymentDate: MutableLiveData<String> = MutableLiveData("")

    val reOrderLivedata : LiveData<Response<JsonobjectModel>>
        get() = orderDetailsRepository.reOrder

    fun reOrder() {
        val json = commonFunctions.commonJsonData()
        json.addProperty("order_id", id.value)
        viewModelScope.launch {
            orderDetailsRepository.getReorderResponse(json, commonFunctions.commonHeader())
        }
    }

}