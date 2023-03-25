package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.ManageProfitLossDetailsRepository
import com.merkaaz.app.utils.CommonFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageProfitLossDetailsViewModel @Inject constructor(
    private val repository: ManageProfitLossDetailsRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {

    var orderId: MutableLiveData<String> = MutableLiveData("")
    var orderDate: MutableLiveData<String> = MutableLiveData("")
    var items: MutableLiveData<String> = MutableLiveData("")
    var orderCostPrice: MutableLiveData<String> = MutableLiveData("")
    var profit: MutableLiveData<String> = MutableLiveData("")
    var grossProfit: MutableLiveData<String> = MutableLiveData("")

    //For Profit Loss Details List
    val profitLossDetailsListLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.profitLossDetailsList


    //For Profit Loss Save
    val profitLossSaveLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.profitLossSave


    fun getProfitLossDetails(orderId: String) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("order_id", orderId)
        viewModelScope.launch {
            repository.getProfitLossDetailsListResponse(json, commonFunctions.commonHeader())
        }
    }


    fun getProfitLossSaveDetails(json: JsonObject) {

        viewModelScope.launch {
            repository.getProfitLossSaveResponse(json, commonFunctions.commonHeader())
        }
    }
}