package com.salonsolution.app.data.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CountryListModel
import com.salonsolution.app.data.model.LoginModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("customer/login")
    suspend fun login(@Body loginRequest: JsonObject): Response<BaseResponse<LoginModel>>

    @POST("customer/register")
    suspend fun register(@Body registerRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/verifyotp")
    suspend fun verifyOtp(@Body verifyOtpRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/resendotp")
    suspend fun resendOtp(@Body resendOtpRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/recoverylink")
    suspend fun forgotPassword(@Body forgotPasswordRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/resetPassword")
    suspend fun resetPassword(@Body resetPasswordRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/countries")
    suspend fun countries(@Body countriesRequest: JsonObject): Response<BaseResponse<CountryListModel>>
}