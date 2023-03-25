package com.salonsolution.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.CmsModel
import com.salonsolution.app.data.model.CountryListModel
import com.salonsolution.app.data.model.NotificationItemCountModel
import com.salonsolution.app.data.model.ProfileDetailsModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.ApiService
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.data.preferences.UserSharedPref
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val apiService: ApiService,
    private val userSharedPref: UserSharedPref
) {


    private val _updateProfileLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val updateProfileLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _updateProfileLiveData

    private val _updatePasswordLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val updatePasswordLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _updatePasswordLiveData

    private val _profileDetailsLiveData =
        MutableLiveData<ResponseState<BaseResponse<ProfileDetailsModel>>>()
    val profileDetailsLiveData: LiveData<ResponseState<BaseResponse<ProfileDetailsModel>>>
        get() = _profileDetailsLiveData

    private val _logoutLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val logoutLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _logoutLiveData

    private val _profileDeleteLiveData =
        MutableLiveData<ResponseState<BaseResponse<JsonElement>>>()
    val profileDeleteLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = _profileDeleteLiveData

    private val _notificationCountLiveData =
        MutableLiveData<ResponseState<BaseResponse<NotificationItemCountModel>>>()
    val notificationCountLiveData: LiveData<ResponseState<BaseResponse<NotificationItemCountModel>>>
        get() = _notificationCountLiveData

    private val _cmsDetailsLiveData =
        MutableLiveData<ResponseState<BaseResponse<CmsModel>>>()
    val cmsDetailsLiveData: LiveData<ResponseState<BaseResponse<CmsModel>>>
        get() = _cmsDetailsLiveData

    private val _countriesDetailsLiveData =
        MutableLiveData<ResponseState<BaseResponse<CountryListModel>>>()
    val countriesDetailsLiveData: LiveData<ResponseState<BaseResponse<CountryListModel>>>
        get() = _countriesDetailsLiveData


    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNo: String,
        countryCode: String,
        emailId: String,
        address1: String,
        address2: String,
        deviceOs: String,
        deviceVersion: String,
        profileImage: String?
    ) {

        val imageFile = if (profileImage.isNullOrEmpty()) {
            val requestFileFront = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("profile_image", "", requestFileFront)
        } else {
            val mFile = File(profileImage)
            val requestFileFront = mFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                "profile_image",
                mFile.name,
                requestFileFront
            )
        }
        val mFirstName: RequestBody =
            firstName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mLastName: RequestBody =
            lastName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPhoneNo: RequestBody =
            phoneNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mCountryCode: RequestBody =
            countryCode.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mEmailId: RequestBody =
            emailId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mAddress1: RequestBody =
            address1.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mAddress2: RequestBody =
            address2.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mDeviceOs: RequestBody =
            deviceOs.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mDeviceVersion: RequestBody =
            deviceVersion.toRequestBody("multipart/form-data".toMediaTypeOrNull())


        _updateProfileLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.updateProfile(
                mFirstName,
                mLastName,
                mPhoneNo,
                mCountryCode,
                mEmailId,
                mAddress1,
                mAddress2,
                mDeviceOs,
                mDeviceVersion,
                imageFile
            )
            _updateProfileLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _updateProfileLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun updatePassword(updatePasswordRequest: JsonObject) {
        _updatePasswordLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.updatePassword(updatePasswordRequest)
            _updatePasswordLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _updatePasswordLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun profileDetails(profileDetailsRequest: JsonObject) {
        _profileDetailsLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.profileDetails(profileDetailsRequest)
            _profileDetailsLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _profileDetailsLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun logout(logoutRequest: JsonObject) {
        _logoutLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.logout(logoutRequest)
            _logoutLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _logoutLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun profileDelete(profileDeleteRequest: JsonObject) {
        _profileDeleteLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.profileDelete(profileDeleteRequest)
            _profileDeleteLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _profileDeleteLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun notificationCount(notificationCountRequest: JsonObject) {
        _notificationCountLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.notificationCount(notificationCountRequest)
            _notificationCountLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _notificationCountLiveData.postValue(ResponseState.create(throwable))
        }

    }

    suspend fun cmsDetails(cmsDetailsRequest: JsonObject) {
        _cmsDetailsLiveData.postValue(ResponseState.Loading())
        try {
            val response = userApi.cmsDetails(cmsDetailsRequest)
            _cmsDetailsLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _cmsDetailsLiveData.postValue(ResponseState.create(throwable))
        }

    }


    suspend fun countries(countriesRequest: JsonObject) {
        _countriesDetailsLiveData.postValue(ResponseState.Loading())
        try {
            val response = apiService.countries(countriesRequest)
            _countriesDetailsLiveData.postValue(ResponseState.create(response, userSharedPref))

        } catch (throwable: Throwable) {
            _countriesDetailsLiveData.postValue(ResponseState.create(throwable))
        }

    }

}