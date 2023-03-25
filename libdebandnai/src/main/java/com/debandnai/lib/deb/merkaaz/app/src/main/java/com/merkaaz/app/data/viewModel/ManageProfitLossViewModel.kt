package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.OrderList
import com.merkaaz.app.data.model.ProfitLossList
import com.merkaaz.app.repository.ManageProfitLossRepository
import com.merkaaz.app.utils.CommonFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ManageProfitLossViewModel @Inject constructor(
    private val repository: ManageProfitLossRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {

    var data_list : LiveData<PagingData<ProfitLossList>> = MutableLiveData()

    fun getData(jsonBody: JsonObject) {
        repository._currentQuery.value = jsonBody
        data_list = repository.getData(commonFunctions.commonHeader()).cachedIn(viewModelScope)
    }


}