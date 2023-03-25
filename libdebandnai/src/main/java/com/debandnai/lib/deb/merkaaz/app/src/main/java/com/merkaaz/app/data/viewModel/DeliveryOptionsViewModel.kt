package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkaaz.app.data.model.DeliveryOptionsModel
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.DeliveryOptionsRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryOptionsViewModel @Inject constructor(
    private val repository: DeliveryOptionsRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context
) : ViewModel() {

    //"delivery_method":1/2, // 1 -> Pick Up; 2 -> Shipping
    var deliveryMethod: MutableLiveData<Int> = MutableLiveData()
    var delivery_options: MutableLiveData<DeliveryOptionsModel> = MutableLiveData()

    var grandTotal: MutableLiveData<String> = MutableLiveData("")
    var total: MutableLiveData<String> = MutableLiveData("")



    //For Delivery Options
    val deliveryOptionsData: LiveData<Response<JsonobjectModel>>
        get() = repository.deliveryOptions


    fun getDeliveryOptions(){
        val json = commonFunctions.commonJsonData()
        json.addProperty("delivery_method", 2)
        viewModelScope.launch {
            repository.getDeliveryOptionsResponse(json,commonFunctions.commonHeader())
        }
    }


}

