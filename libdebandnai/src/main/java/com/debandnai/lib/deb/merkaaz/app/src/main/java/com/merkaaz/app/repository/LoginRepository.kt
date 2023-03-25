package com.merkaaz.app.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.merkaaz.app.data.genericmodel.BaseResponse
import com.merkaaz.app.network.Response
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import retrofit2.HttpException
import javax.inject.Inject


class LoginRepository @Inject constructor(private val api: RetroApi) {

//    private val _login = MutableLiveData<Response<JsonobjectModel>>()
//    public val login: LiveData<Response<JsonobjectModel>>
//        get() = _login

//    suspend fun getLoginResponse(json: JsonObject?) {
//        json?.let {
//            Log.d("response header", it.toString())
//            _login.postValue(Response.Loading())
//            try {
//                val res = api.getlogin(it)
//                if (res.isSuccessful)
//                    _login.postValue(Response.Success(res.body()))
//                else {
//                    res.errorBody()?.let { err ->
//                        _login.postValue(
//                            Response.Error(
//                                MethodClass.get_error_method(err),
//                                res.code()
//                            )
//                        )
//                    }
//                }
//
//            } catch (httpEx: HttpException) {
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

    private val _login = MutableLiveData<Response<BaseResponse<LoginModel>>>()
    public val login: LiveData<Response<BaseResponse<LoginModel>>>
        get() = _login

    suspend fun getLoginResponse(json: JsonObject?) {
        json?.let {
            Log.d("response header", it.toString())
            _login.postValue(Response.Loading())
            try {
                val res = api.getlogin(it)
                if (res.isSuccessful)
                    _login.postValue(Response.Success(res.body()))
                else {
                    res.errorBody()?.let { err ->
                        _login.postValue(
                            Response.Error(
                                MethodClass.get_error_method(err),
                                res.code()
                            )
                        )
                    }
                }

            } catch (httpEx: HttpException) {
                _login.postValue(Response.Error(Constants.HTTP_ERROR, -1))

            } catch (e: Exception) {
                Log.d("error", e.toString())
                _login.postValue(Response.Error(Constants.SERVER_ERROR, -1))
            }

        }

    }

}