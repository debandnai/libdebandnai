package com.merkaaz.app.repository;

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject


class OtpRepository  @Inject constructor(private val api : RetroApi) {

    private val _otp = MutableLiveData<Response<JsonobjectModel>>()
    val otp : LiveData<Response<JsonobjectModel>>
        get() = _otp

//    private val _login = MutableLiveData<Response<JsonobjectModel>>()
//    val login : LiveData<Response<JsonobjectModel>>
//        get() = _login

//    suspend fun getLoginResponse(json: JsonObject?) {
//        json?.let {
//            Log.d("response header", it.toString())
//            _login.postValue(Response.Loading())
//            val res = api.getlogin(it)
//            try {
//                if (res.isSuccessful)
//                    _login.postValue(Response.Success(res.body()))
//                else{
//                    res.errorBody()?.let { err->
//                        _login.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
//                    }
//                }
//
//            }catch (httpEx: HttpException) {
//                _login.postValue(Response.Error(Constants.HTTP_ERROR, -1))
//
//            } catch (e: Exception) {
//                Log.d("error", e.toString())
//                _login.postValue(Response.Error(Constants.SERVER_ERROR, -1))
//            }
//
//        }
//
//    }
    suspend fun getOTPResponse(json: JsonObject?) {
        json?.let {
            Log.d("response header", it.toString())
            _otp.postValue(Response.Loading())
            try {
                val res = api.getotp(it)
                if (res.isSuccessful)
                    _otp.postValue(Response.Success(res.body()))
                else{
                    res.errorBody()?.let { err->
                        _otp.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }
            }catch (httpEx: HttpException) {
                _otp.postValue(Response.Error(Constants.HTTP_ERROR, -1))

            } catch (e: Exception) {
                Log.d("error", e.toString())
                _otp.postValue(Response.Error(Constants.SERVER_ERROR, -1))
            }

        }

    }

}