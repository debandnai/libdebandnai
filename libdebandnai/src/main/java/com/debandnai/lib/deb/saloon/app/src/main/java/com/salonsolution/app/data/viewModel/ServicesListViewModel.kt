package com.salonsolution.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CategoriesModel
import com.salonsolution.app.data.model.CategoryListModel
import com.salonsolution.app.data.model.ServicesList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.repository.ServiceRepository
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesListViewModel @Inject constructor(
    private val repository: ServiceRepository,
    private val requestBodyHelper: RequestBodyHelper
) :
    ViewModel() {

    var selectedPosition: Int = 0
    val categoriesList: MutableLiveData<ArrayList<CategoryListModel>> =
        MutableLiveData(arrayListOf())
    val selectedCategory: MutableLiveData<CategoryListModel> = MutableLiveData()
    val selectedCategoryId = MutableLiveData(-1)
    var serviceListLiveData: LiveData<PagingData<ServicesList>> = MutableLiveData()

    val totalServicesCount: LiveData<String> = repository.totalServicesCountLiveData

    //    "sortField": "price/name",
//    "sortOrder": 1/-1, //1 -> ASC, -1 -> DESC
    val sortBy: MutableLiveData<Pair<String, Int>> = MutableLiveData(Pair(Constants.PRICE, -1))
    val categoriesResponseLiveData = repository.categoriesResponseLiveData

    fun setCategoriesData(categoriesModel: CategoriesModel?) {
        if(categoriesModel!=null)
            updateCategoriesList(categoriesModel.categoryListModel)
        else
            allCategories(requestBodyHelper.getAllCategoriesRequest())
    }

    fun getServiceList(serviceListRequest: JsonObject) {
        repository.serviceListRequest.value = serviceListRequest
        serviceListLiveData = repository.serviceList().cachedIn(viewModelScope)
    }

    private fun allCategories(categoriesRequest: JsonObject) {
        viewModelScope.launch {
            repository.allCategories(categoriesRequest)
        }
    }


    private fun updateCategoriesList(
        categoryListModel: ArrayList<CategoryListModel>
    ) {
        var mPosition =0
        var mSelectedCategory = if(categoryListModel.isNotEmpty())categoryListModel[0] else CategoryListModel()
        for (i in categoryListModel.indices) {
            if (categoryListModel[i].id == selectedCategoryId.value) {
                categoryListModel[i].isSelected = true
                mPosition = i
                mSelectedCategory = categoryListModel[i]
            } else {
                categoryListModel[i].isSelected = false
            }
        }
        categoriesList.value = categoryListModel
        selectedPosition = mPosition
        selectedCategory.value = mSelectedCategory
    }

    fun categoriesItemClick(selectedCategory: CategoryListModel?) {
        categoriesList.value?.let {
            selectedCategoryId.value = selectedCategory?.id
            updateCategoriesList(it)
        }
    }

}