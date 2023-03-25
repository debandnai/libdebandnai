package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.CartRepository
import com.merkaaz.app.repository.CategoryRepository
import com.merkaaz.app.repository.PaymentRepository
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val paymentRepository: PaymentRepository,
    private val categoryRepository: CategoryRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {

    var cartTotal: MutableLiveData<String> = MutableLiveData()
    var minOrderAmount: MutableLiveData<String> = MutableLiveData()
    var isEnableCheckout: MutableLiveData<Boolean> = MutableLiveData(true)
    var isShowMinOrder: MutableLiveData<Boolean> = MutableLiveData(false)
    var isShowNoDataFound: MutableLiveData<Boolean> = MutableLiveData(false)
    var isShowMainLayout: MutableLiveData<Boolean> = MutableLiveData(false)
    val moveOnPriceLiveData: LiveData<Response<JsonobjectModel>>
        get() = paymentRepository.moveOnPriceLiveData
    //add update live data
    val addUpdateLiveData: LiveData<Response<JsonobjectModel>>
        get() = categoryRepository.add_update_product

    //cart avialability data
    val cartProductAvailabilityCheckLiveData: LiveData<Response<JsonobjectModel>>
        get() = cartRepository.cartProductAvailabilityCheckResponse

    //cart list
    val cartListResponse: LiveData<Response<JsonobjectModel>>
        get() = cartRepository.cartListResponse

    fun getCartList() {
        viewModelScope.launch {
            cartRepository.cartList(
                commonFunctions.commonJsonData(),
                commonFunctions.commonHeader()
            )
        }
    }

    fun cartProductAvailabilityCheck() {
        viewModelScope.launch {
            cartRepository.cartProductAvailabilityCheck(
                commonFunctions.commonJsonData(),
                commonFunctions.commonHeader()
            )
        }
    }

    //Add Update product
    fun addUpdateProduct(jsonBody: JsonObject) {
        viewModelScope.launch {
            categoryRepository.add_update(jsonBody, commonFunctions.commonHeader())
        }
    }

    //remove product
    fun removeProduct(jsonBody: JsonObject) {
        viewModelScope.launch {
            categoryRepository.remove_product(jsonBody, commonFunctions.commonHeader())
        }
    }

    //click
    fun continueShoppingClick(view: View) {
        view.context.startActivity(
            Intent(view.context, DashBoardActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        )
    }
    fun getMoveonPrice(delivery_method : Int) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("delivery_method", delivery_method)
        viewModelScope.launch {
            paymentRepository.moveonPrice(json, commonFunctions.commonHeader())
        }
    }
}