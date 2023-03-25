package com.movie.myapplication.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie.myapplication.data.model.popularMovie.popularMolieList
import com.movie.myapplication.data.repository.HomeRepository
import com.movie.myapplication.db.entity.Auth
import com.movie.myapplication.network.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(private val homeRepository: HomeRepository):ViewModel() {
    var showHideTitle: MutableLiveData<Boolean> = MutableLiveData(false)
    val productListResponseLiveData: LiveData<ResponseState<popularMolieList>>
        get() = homeRepository.productListLiveData

    val trandingtListResponseLiveData: LiveData<ResponseState<popularMolieList>>
        get() = homeRepository.trandingMovieListLiveData

    fun getPopularMovieList() {
        viewModelScope.launch {
            homeRepository.popularMovieList()
        }
    }

    fun getTrandingMovieList() {
        viewModelScope.launch {
            homeRepository.trandingMovieList()
        }
    }
    fun logoutAllUser() =
        viewModelScope.launch(Dispatchers.IO) { homeRepository.logoutAllUser() }




}