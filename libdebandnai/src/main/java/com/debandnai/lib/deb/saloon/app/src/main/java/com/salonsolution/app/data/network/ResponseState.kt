package com.salonsolution.app.data.network

import android.util.Log
import com.salonsolution.app.data.preferences.UserSharedPref
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

sealed class ResponseState<T>(
    val data: T? = null,
    val isNetworkError: Boolean = false,
    val errorMessage: String? = null,
    val errorCode: Int? = null
) {

    class Loading<T> : ResponseState<T>()
    class Success<T>(data: T? = null) : ResponseState<T>(data = data)
    class Error<T>(isNetworkError: Boolean, errorMessage: String?, errorCode: Int?) :
        ResponseState<T>(isNetworkError = isNetworkError, errorMessage = errorMessage, errorCode = errorCode)

    companion object {
        private const val TAG: String = "AppDebug"

        fun <T> create(throwable: Throwable): Error<T> {
            return when(throwable){
                is HttpException -> {
                    Log.d(TAG, "GenericApiResponse: Error: ${throwable.code()}: ${throwable.message()}")
                    Error(false, throwable.message(), throwable.code())
                }
                is IOException -> {
                    //network error
                    Log.d(TAG, "GenericApiResponse: Network ")
                    Error(true, null, null)
                }
                else -> {
                    //other exceptions
                    Log.d(TAG, "GenericApiResponse: other Error")
                    Error(false, null, null)
                }
            }
        }

        fun <T> create(response: Response<T>, userSharedPref: UserSharedPref): ResponseState<T> {

            Log.d(TAG, "GenericApiResponse: response: ${response.body().toString()}")
            Log.d(TAG, "GenericApiResponse: raw: ${response.raw()}")
            Log.d(TAG, "GenericApiResponse: headers: ${response.headers()}")
            Log.d(TAG, "GenericApiResponse: message: ${response.message()}")

            return if(response.isSuccessful && response.body()!=null){
                // success
//                val successResponse = Gson().toJson(response.body())
//                val jObj = JSONObject(successResponse)
//                val mResponse = jObj.optJSONObject("response")
//                val raws = mResponse?.optJSONObject("raws")
//                val data = raws?.optJSONObject("data")
//                val token = data?.optString("token")
//                if (!token.isNullOrEmpty()) {
//                    userSharedPref.setUserToken(token)
//                }
                Success(response.body())
            }
            else {
                //Error
                val errorObj =
                    JSONObject(response.errorBody()?.charStream()?.readText()?:"{}")
                val responseObj = errorObj.optJSONObject("response")
                val responseStatus = responseObj?.optJSONObject("status")
                val errorMessage = responseStatus?.optString("msg")
                val errorCode = response.code()
                Error(false,errorMessage,errorCode)
            }
        }
    }
}

