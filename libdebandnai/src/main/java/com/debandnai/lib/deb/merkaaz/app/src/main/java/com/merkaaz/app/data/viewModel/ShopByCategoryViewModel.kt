package com.merkaaz.app.data.viewModel


import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.ShopByCategoryList
import com.merkaaz.app.repository.ShopByCategoryRepository
import com.merkaaz.app.utils.CommonFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ShopByCategoryViewModel @Inject constructor(
    private val repository: ShopByCategoryRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context
) : ViewModel() {
    var data_list: LiveData<PagingData<ShopByCategoryList>> = MutableLiveData()

    fun getData(jsonBody: JsonObject) {
        repository._currentQuery.value = jsonBody
        data_list = repository.getData(commonFunctions.commonHeader()).cachedIn(viewModelScope)
    }

}