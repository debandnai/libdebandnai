package com.merkaaz.app.network


sealed class Response<T>(val data : T? = null, val errorMessage : String? = null, val errorCode: Int? = null){
    class Loading<T>() :Response<T>()
    class Success<T>(data: T? = null):Response<T>(data = data)
    class Error<T>(errorMessage: String, errorCode: Int):Response<T>(errorMessage = errorMessage, errorCode = errorCode)

}
