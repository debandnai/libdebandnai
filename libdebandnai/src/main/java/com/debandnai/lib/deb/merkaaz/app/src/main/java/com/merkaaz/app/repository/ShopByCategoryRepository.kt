package com.merkaaz.app.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.ShopByCategoryList
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.paging.ShopByCategoryPagingSource
import com.merkaaz.app.utils.Constants
import javax.inject.Inject


class ShopByCategoryRepository @Inject constructor(private val api : RetroApi) {
    val _currentQuery = MutableLiveData<JsonObject?>()
    fun getData(header: HashMap<String, String>):
            LiveData<PagingData<ShopByCategoryList>> = _currentQuery.switchMap { query->
        Pager(
            config = PagingConfig(pageSize = Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE),
            pagingSourceFactory = { ShopByCategoryPagingSource(api,query,header) }
        ).liveData
    }

}