package com.salonsolution.app.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.FoodImages
import com.salonsolution.app.data.model.ProductImages
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailsViewModel @Inject constructor(private val repository: FoodRepository, private val cartRepository: CartRepository) :
    ViewModel() {


    var foodId:Int = -1
    var serviceId: Int = -1
    var staffId: Int = -1
    var foodDetailsResponseLiveData = repository.foodDetailsResponseLiveData
    var images:  ArrayList<FoodImages> = arrayListOf()
    var foodAddResponseLiveData = cartRepository.foodAddResponseLiveData
    var foodUpdateResponseLiveData = cartRepository.foodUpdateResponseLiveData

    fun foodDetails(foodDetailsRequest: JsonObject) {
        viewModelScope.launch {
            repository.foodDetails(foodDetailsRequest)
        }
    }

    fun addFood(foodAddRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.foodAdd(foodAddRequest)
        }
    }

    fun updateFood(updateFoodRequest: JsonObject){
        viewModelScope.launch {
            cartRepository.foodUpdate(updateFoodRequest)
        }
    }

}