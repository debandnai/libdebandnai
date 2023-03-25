package com.merkaaz.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.data.model.JsonobjectModel
import javax.inject.Inject

class BestSellerRepository @Inject constructor(private val api: RetroApi) {
    private val _bestSellerResponse = MutableLiveData<Response<JsonobjectModel>>()
    val bestSellerResponse: LiveData<Response<JsonobjectModel>>
        get() = _bestSellerResponse
}