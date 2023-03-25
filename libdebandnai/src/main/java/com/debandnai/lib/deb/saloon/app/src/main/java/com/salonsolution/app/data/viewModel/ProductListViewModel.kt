package com.salonsolution.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.ProductList
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.ProductRepository
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(private val productRepository: ProductRepository, private val cartRepository: CartRepository) :
    ViewModel() {

    var serviceId: Int = -1
    var staffId: Int = -1
    var productListLiveData: LiveData<PagingData<ProductList>> = MutableLiveData()
    var selectedProduct: ProductList? = null

    val totalProductCount: LiveData<String> = productRepository.totalProductCountLiveData
//    "sortField": "price/name",
//    "sortOrder": 1/-1, //1 -> ASC, -1 -> DESC
    val sortBy: MutableLiveData<Pair<String,Int>> = MutableLiveData(Pair(Constants.PRICE,-1))

    var productAddResponseLiveData = cartRepository.productAddResponseLiveData
    var doNotNeedResponseLiveData = cartRepository.doNotNeedResponseLiveData

    fun getProductList(productListRequest: JsonObject) {
        productRepository.productListRequest.value = productListRequest
        productListLiveData = productRepository.productList().cachedIn(viewModelScope)
    }

    fun addProduct(productAddRequest: JsonObject) {
       viewModelScope.launch {
           cartRepository.productAdd(productAddRequest)
       }
    }

    fun doNotNeed(doNotNeedRequest: JsonObject){
        viewModelScope.launch {
            cartRepository.doNotNeed(doNotNeedRequest)
        }
    }

    fun clearUpdateState() {
        cartRepository.clearUpdateState()
    }


}