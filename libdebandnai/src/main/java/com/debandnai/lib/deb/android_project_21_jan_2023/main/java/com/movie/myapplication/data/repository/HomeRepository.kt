package com.movie.myapplication.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.movie.myapplication.BuildConfig
import com.movie.myapplication.data.model.popularMovie.popularMolieList
import com.movie.myapplication.db.UserDao
import com.movie.myapplication.network.ApiService
import com.movie.myapplication.network.ResponseState
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService,private val userDao: UserDao) {
    private val _popularMovieListLiveData =
        MutableLiveData<ResponseState<popularMolieList>>()
    val productListLiveData: LiveData<ResponseState<popularMolieList>>
        get() = _popularMovieListLiveData

    private val _trandingMovieListLiveData =
        MutableLiveData<ResponseState<popularMolieList>>()
    val trandingMovieListLiveData: LiveData<ResponseState<popularMolieList>>
        get() = _trandingMovieListLiveData


    suspend fun popularMovieList() {
        try {

            _popularMovieListLiveData.postValue(ResponseState.Loading())
            val response = apiService.getPopularMovieList(BuildConfig.apiKey)
            if (response.isSuccessful) {
                _popularMovieListLiveData.postValue(ResponseState.Success(response.body()))
            } else {
                _popularMovieListLiveData.postValue(ResponseState.Error(null, null))
            }
        } catch (throwable: Throwable) {
            _popularMovieListLiveData.postValue(ResponseState.Error(throwable.message, null))
        }

    }


    suspend fun trandingMovieList() {
        try {
            _trandingMovieListLiveData.postValue(ResponseState.Loading())
            val response = apiService.getTopRatedMovieList(BuildConfig.apiKey)
            if (response.isSuccessful) {
                _trandingMovieListLiveData.postValue(ResponseState.Success(response.body()))
            } else {
                _trandingMovieListLiveData.postValue(ResponseState.Error(null, null))
            }
        } catch (throwable: Throwable) {
            _trandingMovieListLiveData.postValue(ResponseState.Error(throwable.message, null))
        }

    }

    fun logoutAllUser() {
        userDao.logoutAllUser()
    }
}