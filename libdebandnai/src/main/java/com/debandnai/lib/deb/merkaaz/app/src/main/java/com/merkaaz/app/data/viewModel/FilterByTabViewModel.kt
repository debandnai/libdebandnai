package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import com.merkaaz.app.data.model.FilterCategoryModel
import com.merkaaz.app.data.model.FilterListModel
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.FilterByTabRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterByTabViewModel @Inject constructor(
    private val repository: FilterByTabRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context
) : ViewModel() {
    var filterList: ArrayList<FilterCategoryModel> ?= null
    val filter_list_response : LiveData<Response<JsonobjectModel>>
        get() = repository.filter_list_res
    val filter_livedata_api_response : MutableLiveData<FilterListModel?> = MutableLiveData()

    val filter_livedata : MutableLiveData<FilterListModel?> = MutableLiveData()
    val _filter_livedata : MutableLiveData<FilterListModel?> = MutableLiveData()

    val sort_order : MutableLiveData<Int> = MutableLiveData(-1)
    val sort_field : MutableLiveData<String> = MutableLiveData("")

    // Get filter List
    fun getFilterList(categoryId: String?) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("cat_id",categoryId)
        json.addProperty("cat_type", Constants.SUBCATEGORY)
        viewModelScope.launch {
            repository.getFilterListResponse(json, commonFunctions.commonHeader())
        }
    }

}