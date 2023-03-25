package com.movie.myapplication.network

sealed class ResponseState<T>(val data : T?=null ,val errorMessage:String?=null,val errorCode:Int?=null){
    class Loading<T>(): ResponseState<T>()
    class Success<T>(data:T? = null): ResponseState<T>(data=data)
    class Error<T>(errorMessage:String?,errorCode:Int?):
        ResponseState<T>(errorMessage=errorMessage,errorCode=errorCode)
}
