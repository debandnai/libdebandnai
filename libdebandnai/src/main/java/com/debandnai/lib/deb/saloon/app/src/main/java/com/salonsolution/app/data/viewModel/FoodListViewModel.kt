package com.salonsolution.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.FoodList
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.FoodRepository
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(private val foodRepository: FoodRepository, private val cartRepository: CartRepository) :
    ViewModel() {

    var serviceId: Int = -1
    var staffId: Int = -1
    var foodListLiveData: LiveData<PagingData<FoodList>> = MutableLiveData()

    val totalFoodCount: LiveData<String> = foodRepository.totalFoodCountLiveData
//    "sortField": "price/name",
//    "sortOrder": 1/-1, //1 -> ASC, -1 -> DESC
    val sortBy: MutableLiveData<Pair<String,Int>> = MutableLiveData(Pair(Constants.PRICE,-1))

    var foodAddResponseLiveData = cartRepository.foodAddResponseLiveData
    var foodUpdateResponseLiveData = cartRepository.foodUpdateResponseLiveData
    var doNotNeedResponseLiveData = cartRepository.doNotNeedResponseLiveData

    fun getFoodList(foodListRequest: JsonObject) {
        foodRepository.foodListRequest.value = foodListRequest
        foodListLiveData = foodRepository.foodList().cachedIn(viewModelScope)
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

    fun doNotNeed(doNotNeedRequest: JsonObject){
        viewModelScope.launch {
            cartRepository.doNotNeed(doNotNeedRequest)
        }
    }
    fun clearUpdateState() {
        cartRepository.clearUpdateState()
    }


}