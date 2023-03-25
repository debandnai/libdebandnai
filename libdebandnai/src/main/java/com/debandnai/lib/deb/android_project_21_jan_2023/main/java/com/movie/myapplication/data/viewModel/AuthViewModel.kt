package com.movie.myapplication.data.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie.myapplication.R
import com.movie.myapplication.data.repository.AuthRepository
import com.movie.myapplication.db.entity.Auth
import com.movie.myapplication.db.entity.CurrentAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel  @Inject constructor(private val authRepository: AuthRepository,application: Application):ViewModel() {
    private val context = application.applicationContext
    var showHideText: MutableLiveData<String> = MutableLiveData(context.getString(R.string.show))
    val titleTag = MutableLiveData<String>()
    var userName: MutableLiveData<String> = MutableLiveData("")
    var password: MutableLiveData<String> = MutableLiveData("")


    var first_name: MutableLiveData<String> = MutableLiveData("")
    var last_name: MutableLiveData<String> = MutableLiveData("")
    var age: MutableLiveData<String> = MutableLiveData("")
    var gender: MutableLiveData<String> = MutableLiveData("")
    var email: MutableLiveData<String> = MutableLiveData("")
    var phone_number: MutableLiveData<String> = MutableLiveData("")
    var password_sign_up: MutableLiveData<String> = MutableLiveData("")

    val sessionDetailsLiveData: LiveData<List<CurrentAuth>>
        get()=authRepository.getSessionDetails()

    val userDetails: LiveData<List<Auth>>
        get()=authRepository.getUserDetails()




    fun insertAuthDetails(auth: Auth) =
        viewModelScope.launch(Dispatchers.IO) { authRepository.insertUserDetails(auth) }

    fun insertSession(currentAuth: CurrentAuth) =
        viewModelScope.launch(Dispatchers.IO) { authRepository.insertSession(currentAuth) }



    fun updateAuth(auth: Auth) =
        viewModelScope.launch(Dispatchers.IO) { authRepository.updateAuth(auth) }

    fun logoutAllUser() =
        viewModelScope.launch(Dispatchers.IO) { authRepository.logoutAllUser() }



}