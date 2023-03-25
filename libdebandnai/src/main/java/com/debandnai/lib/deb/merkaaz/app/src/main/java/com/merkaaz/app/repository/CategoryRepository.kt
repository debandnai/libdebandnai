package com.merkaaz.app.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.paging.AllProductPagingSource
import com.merkaaz.app.paging.BestFeaturedPagingSource
import com.merkaaz.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val api : RetroApi) {



    //Product subCAtegory List
    private val _subCategoryList = MutableLiveData<Response<JsonobjectModel>>()
    val subCategoryList : LiveData<Response<JsonobjectModel>>
        get() = _subCategoryList

    private val _add_update_product = MutableLiveData<Response<JsonobjectModel>>()
    val add_update_product : LiveData<Response<JsonobjectModel>>
        get() = _add_update_product

    val _currentQuery = MutableLiveData<JsonObject?>()

    private val _filter_list_res = MutableLiveData<Response<JsonobjectModel>>()
    val filter_list_res : LiveData<Response<JsonobjectModel>>
        get() = _filter_list_res

    private val _filter_count_res = MutableLiveData<Response<JsonobjectModel>>()
    val filter_count_res : LiveData<Response<JsonobjectModel>>
        get() = _filter_count_res

    // For cart count
    private val _cartCount = MutableLiveData<Response<JsonobjectModel>>()
    val cartCount : LiveData<Response<JsonobjectModel>>
        get() = _cartCount

    // For search
    private val _searchResult = MutableLiveData<Response<JsonobjectModel>>()
    val searchResult : LiveData<Response<JsonobjectModel>>
        get() = _searchResult

    fun getData(
        header: HashMap<String, String>,
        pageType: Int?
    ):
            LiveData<PagingData<FeaturedData>> = _currentQuery.switchMap { query->
        Pager(
        config = PagingConfig(pageSize = TOTAL_NO_OF_PRODUCTS_PER_PAGE),
        pagingSourceFactory = {BestFeaturedPagingSource(api,query,header,pageType)}
    ).liveData
    }

    fun getAllData(
        header: HashMap<String, String>
    ):
            LiveData<PagingData<FeaturedData>> = _currentQuery.switchMap { query->
        Pager(
        config = PagingConfig(pageSize = TOTAL_NO_OF_PRODUCTS_PER_PAGE),
        pagingSourceFactory = { AllProductPagingSource(api,query,header) }
    ).liveData
    }





    suspend fun getSubCategoryListResponse(
        body: JsonObject?,
        header: HashMap<String, String>,
        categoryId: String?
    ) {
        body?.let {
            Log.d("response header", it.toString())
            _subCategoryList.postValue(Response.Loading())

            try {
                val res = if (categoryId?.isEmpty() == true)
                    api.getAllcategoryList(header, it)
                else
                    api.getcategoryList(header, it)
                if (res.isSuccessful) {
                    Log.d("response body", res.body().toString())
                    _subCategoryList.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _subCategoryList.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                Log.d("error",e.toString())
                _subCategoryList.postValue(Response.Error(e.toString(),-1))
            }

        }

    }

    suspend fun getFilterListResponse(jsonObject: JsonObject, header: java.util.HashMap<String, String>) {
        jsonObject?.let {
            _filter_list_res.postValue(Response.Loading())
            try {
                val res = api.getFilterList(header,it)
                if (res.isSuccessful) {
                    _filter_list_res.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _filter_list_res.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                _filter_list_res.postValue(Response.Error(e.toString(),-1))
            }

        }
    }

    suspend fun getFilterCountResponse(jsonObject: JsonObject, header: java.util.HashMap<String, String>) {
        jsonObject?.let {
            _filter_count_res.postValue(Response.Loading())
            try {
                val res = api.getFilterCount(header,it)

                if (res.isSuccessful) {
                    _filter_count_res.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _filter_count_res.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                _filter_count_res.postValue(Response.Error(e.toString(),-1))
            }

        }
    }


    suspend fun add_update(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            Log.d("response header", it.toString())
            _add_update_product.postValue(Response.Loading())

            try {
                val res = api.add_update(header,it)
                if (res.isSuccessful) {
                    Log.d("response body", res.body().toString())
                    _add_update_product.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _add_update_product.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                Log.d("error",e.toString())
                _add_update_product.postValue(Response.Error(e.toString(),-1))
            }

        }

    }

    suspend fun remove_product(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            Log.d("response header", it.toString())
            _add_update_product.postValue(Response.Loading())

            try {
                val res = api.delete_item(header,it)
                if (res.isSuccessful) {
                    Log.d("response body", res.body().toString())
                    _add_update_product.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _add_update_product.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                Log.d("error",e.toString())
                _add_update_product.postValue(Response.Error(e.toString(),-1))
            }

        }
    }

    // For cart count Response
    suspend fun getCartCountResponse(jsonObject: JsonObject?, header: HashMap<String, String>) {
        jsonObject?.let {
            _cartCount.postValue(Response.Loading())
            try {
                val res = api.getCartCount(header,it)
                if (res.isSuccessful) {
                    _cartCount.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _cartCount.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                _cartCount.postValue(Response.Error(e.toString(),-1))
            }

        }

    }

    suspend fun getsearchResponse(jsonObject: JsonObject, header: java.util.HashMap<String, String>) {
        jsonObject?.let {
            _searchResult.postValue(Response.Loading())
            try {
                val res = api.getSearchData(header,it)
                if (res.isSuccessful) {
                    _searchResult.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _searchResult.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                _searchResult.postValue(Response.Error(e.toString(),-1))
            }

        }
    }




}