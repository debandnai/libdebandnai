package com.salonsolution.app.data.network

import com.google.gson.JsonObject
import com.salonsolution.app.data.model.LoginModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRefreshApi {
    @POST("customer/regeneratetoken")
    suspend fun regenerateToken(@Body regenerateTokenRequest: JsonObject): Response<BaseResponse<LoginModel>>
}