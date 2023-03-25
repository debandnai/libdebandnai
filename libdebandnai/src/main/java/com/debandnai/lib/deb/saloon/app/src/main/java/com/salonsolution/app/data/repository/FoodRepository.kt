package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.FoodDetailsModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.paging.FoodListPagingSource
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.Constants
import javax.inject.Inject

class FoodRepository @Inject constructor(private val userApi: UserApi, private val userSharedPref: UserSharedPref) {

    private val _foodDetailsResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<FoodDetailsModel>>>()
    val foodDetailsResponseLiveData: LiveData<ResponseState<BaseResponse<FoodDetailsModel>>>
        get() = _foodDetailsResponseLiveData

    private val _totalFoodCountLiveData =
        MutableLiveData<String>()
    val totalFoodCountLiveData: LiveData<String>
        get() = _totalFoodCountLiveData

    val foodListRequest = MutableLiveData<JsonObject>()
    fun foodList() = foodListRequest.switchMap { query ->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { FoodListPagingSource(userApi, query, _totalFoodCountLiveData) }
        ).liveData
    }

    suspend fun foodDetails(foodDetailsRequest: JsonObject) {
        _foodDetailsResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.foodDetails(foodDetailsRequest)
            _foodDetailsResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _foodDetailsResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

}