package ie.healthylunch.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionList
import ie.healthylunch.app.data.repository.TransactionListRepository

class TransactionListViewModel(
    private val repository: TransactionListRepository
) : ViewModel() {

    var noDataTextVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var recyclerViewVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var noDataText: MutableLiveData<String> = MutableLiveData("No Data Found")
    var data_list : LiveData<PagingData<TransactionList>> = MutableLiveData()

    fun getData(jsonBody: JsonObject,token: String) {
        repository.transaactionJsonRequest.value = jsonBody
        data_list = repository.getData(token).cachedIn(viewModelScope)
    }
}