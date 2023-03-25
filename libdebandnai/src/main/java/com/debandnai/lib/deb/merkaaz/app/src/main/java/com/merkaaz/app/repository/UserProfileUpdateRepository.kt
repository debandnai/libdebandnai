package com.merkaaz.app.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.MethodClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception
import java.util.HashMap
import javax.inject.Inject


class UserProfileUpdateRepository @Inject constructor(private val api: RetroApi) {

    private val _updateUserProfile = MutableLiveData<Response<JsonobjectModel>>()
    public val updateUserProfile : LiveData<Response<JsonobjectModel>>
        get() = _updateUserProfile

    suspend fun updateUserProfile(
        header: HashMap<String, String>,
        shopName: RequestBody,
        vendorName: RequestBody,
        phone: RequestBody,
        email: RequestBody,
        tax: RequestBody,
        language: RequestBody,
        devOs: RequestBody,
        appVersion: RequestBody,
        image: MultipartBody.Part
    ) {
            _updateUserProfile.postValue(Response.Loading())
            try {
                val res = api.updateUserProfile(header,shopName,vendorName, phone, email, tax,language,devOs, appVersion,image )
                if (res.isSuccessful) {
                    _updateUserProfile.postValue(Response.Success(res.body()))
                }else{
                    res.errorBody()?.let { err->
                        _updateUserProfile.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch(e:Exception) {
                println("Error multi part...${e.toString()}")
                _updateUserProfile.postValue(Response.Error(e.toString(),-1))
            }

    }

}