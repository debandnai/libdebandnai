package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.CartRepository
import com.merkaaz.app.repository.PaymentRepository
import com.merkaaz.app.utils.CommonFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val cartRepository: CartRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {

    var paymentStatus: MutableLiveData<String> = MutableLiveData("2")
    var paymentMethod: MutableLiveData<String> = MutableLiveData("")
    var deliveryMethod: MutableLiveData<Int> = MutableLiveData()
    var totalAmt: MutableLiveData<String> = MutableLiveData("")
    var deliveryAmt: MutableLiveData<String> = MutableLiveData("")
    var grandTotal: MutableLiveData<String> = MutableLiveData("")

    //For Payment Details
//    val paymentDetailsResponse: LiveData<Response<JsonobjectModel>>
//        get() = repository.PaymentDetailsResponse

    //For save
    val saveResponse: LiveData<Response<JsonobjectModel>>
        get() = repository.saveResponse

    val moveOnPrice: LiveData<Response<JsonobjectModel>>
        get() = repository.moveOnPriceLiveData

    //cart avialability data
    val cartProductAvailabilityCheckLiveData: LiveData<Response<JsonobjectModel>>
        get() = cartRepository.cartProductAvailabilityCheckResponse

    //product avialable
    fun cartProductAvailabilityCheck() {
        viewModelScope.launch {
            cartRepository.cartProductAvailabilityCheck(
                commonFunctions.commonJsonData(),
                commonFunctions.commonHeader()
            )
        }
    }

    fun getMoveonPrice(delivery_method : Int) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("delivery_method", delivery_method)
        viewModelScope.launch {
            repository.moveonPrice(json, commonFunctions.commonHeader())
        }
    }

//    fun getPaymentDetails() {
//        val json = commonFunctions.commonJsonData(context)
//        json.addProperty("delivery_method", deliveryMethod.value)
//        viewModelScope.launch {
//            repository.PaymentDetails(json, commonFunctions.commonHeader(context))
//        }
//    }
    fun save_data_afterpayment(payment_method_stat: String, payment_status: Int, delivery_method: Int?) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("payment_method", payment_method_stat)
        json.addProperty("payment_status", payment_status)
        json.addProperty("delivery_method", delivery_method)
        viewModelScope.launch {
            repository.saveOrder(json, commonFunctions.commonHeader())
        }
    }
}

