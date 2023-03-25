package com.salonsolution.app.data.viewModel

import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CartListModel
import com.salonsolution.app.data.model.CartServiceList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.OrderRepository
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderSummaryViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val requestBodyHelper: RequestBodyHelper
) : ViewModel(),  TextView.OnEditorActionListener {

    var cartData :  CartListModel? = null
    var cartServiceList = MutableLiveData(CartServiceList())
    var moreItem = MutableLiveData(0)
    var isCouponApplied = MutableLiveData(false)
    var couponCode= MutableLiveData("")
    var cartTotal= MutableLiveData("")
    var cartActualValue= MutableLiveData("0")
    var couponPrice= MutableLiveData("")
    var grandTotal = MutableLiveData("")
    var product = MutableLiveData("")
    var food = MutableLiveData("")

    //for validation
    var couponError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))

    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    var matchCouponResponseLiveData = cartRepository.matchCouponResponseLiveData
    var checkCartResponseLiveData = orderRepository.checkCartResponseLiveData
    var orderSaveResponseLiveData = orderRepository.orderSaveResponseLiveData


    init {
        errorResetObserver.addSource(couponCode) {
            couponError.value = Pair(false, "")
        }
        errorResetObserver.addSource(cartServiceList){ list ->
            viewModelScope.launch {
               product.value = TextUtils.join(",",list.productList.map { it.productName })
               food.value = TextUtils.join(",",list.foodList.map { it.foodName })
            }
        }
    }

    private fun matchCoupon(matchCouponRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.matchCoupon(matchCouponRequest)
        }
    }
    fun orderSave() {
        viewModelScope.launch {
            orderRepository.orderSave(requestBodyHelper.getOrderSaveRequest(couponCode.value?.trim()))
        }
    }

    fun placeOrderButtonClick(view:View){
        viewModelScope.launch {
            orderRepository.checkCart(requestBodyHelper.getCheckCartRequest())
        }
    }

    fun applyButtonClick(view:View){
        if(isCouponApplied.value!=true && couponValidation(view)){
            matchCoupon(requestBodyHelper.getMatchCouponRequest(couponCode.value?.trim(),cartActualValue.value))
        }
    }

    private fun couponValidation(view: View): Boolean {
        val result: Boolean
        if (couponCode.value.isNullOrEmpty()) {
            couponError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_a_coupon_code))
            result = false
        } else {
            result = true
             UtilsCommon.hideKeyboard(view)
        }

        return result

    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        v?.let {
            Log.d("tag","id: ${it.id}")
            when (actionId) {
                Constants.IME_ACTION_APPLY -> {
                    it.clearFocus()
                    if(couponValidation(it)){
                        applyButtonClick(it)
                        return false
                    }else {
                        return true
                    }

                }
                else -> {
                    return false
                }
            }
        }

        return false
    }


}