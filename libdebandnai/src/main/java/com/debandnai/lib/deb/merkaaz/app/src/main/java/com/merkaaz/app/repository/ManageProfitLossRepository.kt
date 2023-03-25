package com.merkaaz.app.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.OrderList
import com.merkaaz.app.data.model.ProfitLossList
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.paging.ProfitLossPagingSource
import com.merkaaz.app.utils.Constants
import javax.inject.Inject


class ManageProfitLossRepository @Inject constructor(private val retroApi: RetroApi) {
    val _currentQuery = MutableLiveData<JsonObject?>()
    fun getData(header: HashMap<String, String>):
            LiveData<PagingData<ProfitLossList>> = _currentQuery.switchMap { query->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { ProfitLossPagingSource(retroApi,query,header) }
        ).liveData
    }

}