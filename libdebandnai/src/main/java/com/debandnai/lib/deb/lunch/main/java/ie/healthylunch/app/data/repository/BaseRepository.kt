package ie.healthylunch.app.data.repository


import android.util.Log
import ie.healthylunch.app.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


abstract class BaseRepository {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        val errorString = throwable.response()?.errorBody()?.string()
                        throwable.response()?.errorBody()?.string()?.let { Log.e("err", it) }
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody(), errorString)
                    }
                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }
}