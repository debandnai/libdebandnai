package ie.healthylunch.app.data.network

import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure<out T>(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?,
        val errorString: String? = null
    ) : Resource<T>()
}