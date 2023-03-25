package com.merkaaz.app.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.CategoryRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductBottomSheetViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context
) : ViewModel() {

    val addUpdateLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.add_update_product

    //Add   Update
    fun add_update_Product(jsonBody: JsonObject) {
        viewModelScope.launch {
            repository.add_update(jsonBody, commonFunctions.commonHeader())
        }
    }

    fun remove_Product(jsonBody: JsonObject) {
        viewModelScope.launch {
            repository.remove_product(jsonBody,commonFunctions.commonHeader())
        }
    }

}

