package com.movie.myapplication.data.repository

import androidx.lifecycle.LiveData
import com.movie.myapplication.db.UserDao
import com.movie.myapplication.db.entity.Auth
import com.movie.myapplication.db.entity.CurrentAuth
import javax.inject.Inject

class AuthRepository @Inject constructor(private val userDao: UserDao){
    fun getSessionDetails(): LiveData<List<CurrentAuth>> = userDao.getSessionDetails()
    fun getUserDetails(): LiveData<List<Auth>> = userDao.getUserDetails()

    suspend fun insertUserDetails(event: Auth) {
        userDao.insertUserDetails(event)
    }

    suspend fun insertSession(currentAuth: CurrentAuth) {
        userDao.insertSession(currentAuth)
    }



    suspend fun updateAuth(auth: Auth) {
        userDao.updateAuth(auth)
    }

     fun logoutAllUser() {
        userDao.logoutAllUser()
    }


}