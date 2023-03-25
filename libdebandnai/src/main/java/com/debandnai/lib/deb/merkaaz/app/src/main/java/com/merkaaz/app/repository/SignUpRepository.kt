package com.merkaaz.app.repository

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

class SignUpRepository @Inject constructor(private val api : RetroApi) {

    private val _signup = MutableLiveData<Response<JsonobjectModel>>()
    val signup : LiveData<Response<JsonobjectModel>>
        get() = _signup

    suspend fun getsignup(json: JsonObject?) {
        json?.let {
            Log.d("response header", it.toString())
            _signup.postValue(Response.Loading())
            try {
                val res = api.getregistration(it)
                if (res.isSuccessful)
                    _signup.postValue(Response.Success(res.body()))
                else{
                    res.errorBody()?.let { err->
                        _signup.postValue(Response.Error(MethodClass.get_error_method(err),res.code()))
                    }
                }

            }catch (httpEx: HttpException) {
                _signup.postValue(Response.Error(Constants.HTTP_ERROR, -1))

            } catch (e: Exception) {
                Log.d("error", e.toString())
                _signup.postValue(Response.Error(Constants.SERVER_ERROR, -1))
            }

        }

    }

}