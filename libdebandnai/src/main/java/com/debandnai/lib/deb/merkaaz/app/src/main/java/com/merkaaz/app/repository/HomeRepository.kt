package com.merkaaz.app.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.utils.MethodClass
import javax.inject.Inject


class HomeRepository @Inject constructor(private val api: RetroApi) {
    // For Category List
    private val _categoryList = MutableLiveData<Response<JsonobjectModel>>()
    val categoryList: LiveData<Response<JsonobjectModel>>
        get() = _categoryList

    // For Featured List
    private val _featuredList = MutableLiveData<Response<JsonobjectModel>>()
    val featuredList: LiveData<Response<JsonobjectModel>>
        get() = _featuredList

    // For Best Seller List
    private val _bestSellerList = MutableLiveData<Response<JsonobjectModel>>()
    val bestSellerList: LiveData<Response<JsonobjectModel>>
        get() = _bestSellerList

    // For Snacks Branded List
    private val _snacksAndBrandedList = MutableLiveData<Response<JsonobjectModel>>()
    val snacksAndBrandedList: LiveData<Response<JsonobjectModel>>
        get() = _snacksAndBrandedList


    // For Category List Response
    suspend fun getCategoryListResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            Log.d("response header home cat", it.toString())
            _categoryList.postValue(Response.Loading())

            try {
                val res = api.getcategoryList(header, it)
                if (res.isSuccessful) {
                    Log.d("response body", res.body().toString())
                    _categoryList.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _categoryList.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("error", e.toString())
                _categoryList.postValue(Response.Error(e.toString(), -1))
            }

        }

    }


    // For Featured List Response
    suspend fun getFeaturedListResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _featuredList.postValue(Response.Loading())
            try {
                val res = api.getHomeFeaturedProducts(header, it)
                if (res.isSuccessful) {
                    _featuredList.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _featuredList.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _featuredList.postValue(Response.Error(e.toString(), -1))
            }

        }

    }

    // For Best Seller List Response
    suspend fun getBestSellerListResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _bestSellerList.postValue(Response.Loading())
            try {
                val res = api.getHomeFeaturedProducts(header, it)
                if (res.isSuccessful) {
                    _bestSellerList.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _bestSellerList.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _bestSellerList.postValue(Response.Error(e.toString(), -1))
            }

        }

    }

    //For Snacks and Branded List Response
    suspend fun getSnacksAndBrandedListResponse(body: JsonObject?, header: HashMap<String, String>) {
        body?.let {
            _snacksAndBrandedList.postValue(Response.Loading())
            try {
                val res = api.dashboardProductList(header, it)
                if (res.isSuccessful) {
                    _snacksAndBrandedList.postValue(Response.Success(res.body()))
                } else {
                    res.errorBody()?.let { err ->
                        _snacksAndBrandedList.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _snacksAndBrandedList.postValue(Response.Error(e.toString(), -1))
            }

        }

    }

}