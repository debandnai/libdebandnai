package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.LoginModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ApiService
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.UtilsCommon.clear
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService, private val userSharedPref: UserSharedPref) {

    private val _loginResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<LoginModel>>>()
    val loginResponseLiveData: LiveData<ResponseState<BaseResponse<LoginModel>>>
        get() = _loginResponseLiveData

    private val _registerResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val registerResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _registerResponseLiveData

    private val _verifyOtpResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val verifyOtpResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _verifyOtpResponseLiveData

    private val _resendOtpResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val resendOtpResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _resendOtpResponseLiveData

    private val _forgotPasswordResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val forgotPasswordResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _forgotPasswordResponseLiveData

    private val _resetPasswordResponseLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val resetPasswordResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _resetPasswordResponseLiveData

    suspend fun login(loginRequest: JsonObject) {
        _loginResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = apiService.login(loginRequest)
            _loginResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _loginResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun register(registerRequest: JsonObject) {
        _registerResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = apiService.register(registerRequest)
            _registerResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _registerResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun verifyOtp(verifyOtpRequest: JsonObject) {
        _verifyOtpResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = apiService.verifyOtp(verifyOtpRequest)
            _verifyOtpResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _verifyOtpResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun resendOtp(resendOtpRequest: JsonObject) {
        _resendOtpResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = apiService.resendOtp(resendOtpRequest)
            _resendOtpResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _resendOtpResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun forgotPassword(forgotPasswordRequest: JsonObject) {
        _forgotPasswordResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = apiService.forgotPassword(forgotPasswordRequest)
            _forgotPasswordResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _forgotPasswordResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun resetPassword(resetPasswordRequest: JsonObject) {
        _resetPasswordResponseLiveData.postValue(ResponseState.Loading())
        try {
            val response = apiService.resetPassword(resetPasswordRequest)
            _resetPasswordResponseLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _resetPasswordResponseLiveData.postValue(ResponseState.create(throwable))
        }

    }

    fun clearUpdateState() {
        _loginResponseLiveData.clear()
        _registerResponseLiveData.clear()
        _verifyOtpResponseLiveData.clear()
        _resendOtpResponseLiveData.clear()
        _forgotPasswordResponseLiveData.clear()
        _resetPasswordResponseLiveData.clear()

    }


}