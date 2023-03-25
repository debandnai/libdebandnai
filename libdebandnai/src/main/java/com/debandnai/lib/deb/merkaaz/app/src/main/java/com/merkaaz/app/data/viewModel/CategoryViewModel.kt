package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.data.model.FilterCategoryModel
import com.merkaaz.app.data.model.FilterListModel
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.CategoryRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context
) : ViewModel() {

    var product_category = -1
    val category_item: MutableLiveData<Int?> = MutableLiveData(0)

    //Sub Category List
    val subcategoryLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.subCategoryList

    val addUpdateLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.add_update_product

    var data_list : LiveData<PagingData<FeaturedData>> = MutableLiveData()

    var filterList: ArrayList<FilterCategoryModel> ?= null
    var filter_list_live_data : MutableLiveData<ArrayList<FilterCategoryModel>?> = MutableLiveData()
    //Api response for filter
    val filter_list_response : LiveData<Response<JsonobjectModel>>
        get() = repository.filter_list_res

    // to set the filter data in tab
    val _filter_livedata : MutableLiveData<FilterListModel> = MutableLiveData()
    val filter_livedata : LiveData<FilterListModel>
    get() = _filter_livedata

    val sort_order : MutableLiveData<Int?> = MutableLiveData()
    val sort_field : MutableLiveData<String?> = MutableLiveData("")
    val sub_cat_id : MutableLiveData<String> = MutableLiveData("")

    val searchResult : LiveData<Response<JsonobjectModel>>
        get() = repository.searchResult

    //Filter count response
    val filterCountLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.filter_count_res
    val filter_count : MutableLiveData<Int> = MutableLiveData(0)
    val filter_applicable : MutableLiveData<Boolean> = MutableLiveData(false)
    //Product Sub Category
    fun getProductSubcategory(categoryId: String?) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("parent_cat_id",categoryId)
        println("body sub cat .... ${json.toString()}")
        viewModelScope.launch {
            repository.getSubCategoryListResponse(json,commonFunctions.commonHeader(),categoryId)
        }
    }

    fun getData(jsonBody: JsonObject, pageType: Int?, categoryId: String?) {
//        try {
        Log.d(TAG, "getData: categoryId $categoryId t")
        repository._currentQuery.value = jsonBody
        data_list = if (categoryId?.isEmpty() != true) {
            repository.getData(commonFunctions.commonHeader(), pageType)
                .cachedIn(viewModelScope)
        } else{
            repository.getAllData(commonFunctions.commonHeader())
                .cachedIn(viewModelScope)
        }
//        }catch(e: Exception){
//            println("error product..${e.toString()}")
//        }
    }

    // Get filter List
    fun getFilterList(categoryId: String?) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("cat_id",categoryId)
        json.addProperty("cat_type", Constants.SUBCATEGORY)
        viewModelScope.launch {
            repository.getFilterListResponse(json, commonFunctions.commonHeader())
        }
        Log.d(TAG, "getFilterList: "+json)
    }

    // Get filter List
    fun getFilterCount(filter: JsonObject, cat_id: String, cat_type: String) {
        val json = commonFunctions.commonJsonData()
        if (sub_cat_id.value.toString().trim().isNotEmpty()) {
            json.addProperty("cat_id", sub_cat_id.value)
        }else{
            json.addProperty("cat_id", cat_id)
        }
        json.addProperty("cat_type", cat_type)
        json.add("filters", filter)
        viewModelScope.launch {
            repository.getFilterCountResponse(json, commonFunctions.commonHeader())
        }
    }

    //Add   Update
    fun add_update_Product(jsonBody: JsonObject) {
        viewModelScope.launch {
            repository.add_update(jsonBody,commonFunctions.commonHeader())
        }
    }

    fun remove_Product(jsonBody: JsonObject) {
        viewModelScope.launch {
            repository.remove_product(jsonBody,commonFunctions.commonHeader())
        }
    }

    fun getsearchData(value: String?) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("search_term",value)
        viewModelScope.launch {
            repository.getsearchResponse(json, commonFunctions.commonHeader())
        }
    }

}

