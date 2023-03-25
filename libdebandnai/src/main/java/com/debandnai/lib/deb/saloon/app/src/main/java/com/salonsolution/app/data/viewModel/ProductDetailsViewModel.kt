package com.salonsolution.app.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.ProductImages
import com.salonsolution.app.data.model.StaffImages
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(private val repository: ProductRepository, private val cartRepository: CartRepository) :
    ViewModel() {


    var productId:Int = -1
    var serviceId: Int = -1
    var staffId: Int = -1
    var inCart:Int = 0  // 1 -> Added in cart; 0 -> Not in cart
    var images:  ArrayList<ProductImages> = arrayListOf()
    var productDetailsResponseLiveData = repository.productDetailsResponseLiveData
    var productAddResponseLiveData = cartRepository.productAddResponseLiveData

    fun productDetails(productDetailsRequest: JsonObject) {
        viewModelScope.launch {
            repository.productDetails(productDetailsRequest)
        }
    }

    fun addProduct(productAddRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.productAdd(productAddRequest)
        }
    }

    fun clearUpdateState() {
        cartRepository.clearUpdateState()
    }

}