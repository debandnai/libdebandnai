package com.merkaaz.app.data.viewModel


import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.merkaaz.app.network.Response
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.repository.CategoryRepository
import com.merkaaz.app.repository.ProductDetailsRepository
import com.merkaaz.app.utils.CommonFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductDetailsRepository,
    private val categoryRepository: CategoryRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {
    val productDetailsLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.productDetailsResponse

    val cartCountLiveData: LiveData<Response<JsonobjectModel>>
        get() = categoryRepository.cartCount
    val addUpdateLiveData: LiveData<Response<JsonobjectModel>>
        get() = categoryRepository.add_update_product


    val productName: MutableLiveData<String> = MutableLiveData()
    val subCatName: MutableLiveData<String> = MutableLiveData()
    val brandName: MutableLiveData<String> = MutableLiveData()
    val productImageLink: MutableLiveData<String?> = MutableLiveData("")
    val shortDesc: MutableLiveData<String> = MutableLiveData()
    val longDesc: MutableLiveData<String> = MutableLiveData()
    val productOfferPrice: MutableLiveData<String> = MutableLiveData()
    val productSellPrice: MutableLiveData<String> = MutableLiveData()
    val offPercentage: MutableLiveData<String> = MutableLiveData()
    val isDiscounted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOutOfStock: MutableLiveData<Boolean> = MutableLiveData(false)
    val isMainLayoutVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRelatedLayoutVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val cartCount: MutableLiveData<String> = MutableLiveData("0")
    val isCartCountVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var productId: String? = null


    fun productDetails(product_id: String?) {
        product_id?.let {
            val jsonObject = commonFunctions.commonJsonData()
            jsonObject.addProperty("product_id", it)
            viewModelScope.launch {
                repository.getProductDetailsResponse(commonFunctions.commonHeader(), jsonObject)
            }
            Log.d(TAG, "productDetails: jsonObject $jsonObject")
        }
    }


    fun getCartCountResponse() {
        viewModelScope.launch {
            categoryRepository.getCartCountResponse(commonFunctions.commonJsonData(), commonFunctions.commonHeader())
        }
    }

    fun add_update_Product(jsonBody: JsonObject) {
        viewModelScope.launch {
            categoryRepository.add_update(jsonBody,commonFunctions.commonHeader())
        }

        Log.d(TAG, "add_update_Product: jsonBody $jsonBody")
    }

    fun remove_Product(jsonBody: JsonObject) {
        viewModelScope.launch {
            categoryRepository.remove_product(jsonBody,commonFunctions.commonHeader())
        }
    }

}