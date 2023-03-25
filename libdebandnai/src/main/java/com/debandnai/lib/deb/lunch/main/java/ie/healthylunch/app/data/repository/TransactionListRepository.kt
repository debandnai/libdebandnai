package ie.healthylunch.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionList
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.paging.TransactionsPagingSource
import ie.healthylunch.app.utils.Constants.Companion.TOTAL_NO_OF_ITEMS_PER_PAGE

class TransactionListRepository(
    private val apiInterface: ApiInterface
) : BaseRepository() {
    val transaactionJsonRequest = MutableLiveData<JsonObject?>()
    fun getData(token: String):
            LiveData<PagingData<TransactionList>> = transaactionJsonRequest.switchMap { query->
        Pager(
            config = PagingConfig(pageSize = TOTAL_NO_OF_ITEMS_PER_PAGE),
            pagingSourceFactory = { TransactionsPagingSource(apiInterface,query,token) }
        ).liveData
    }
}