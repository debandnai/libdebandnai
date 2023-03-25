package com.salonsolution.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CategoriesModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriseViewModel  @Inject constructor(private val repository: ServiceRepository): ViewModel() {

    var currentCategoriesList: CategoriesModel? = CategoriesModel()

    val categoriesResponseLiveData = repository.categoriesResponseLiveData


    fun allCategories(categoriesRequest: JsonObject) {
        viewModelScope.launch {
            repository.allCategories(categoriesRequest)
        }
    }
}