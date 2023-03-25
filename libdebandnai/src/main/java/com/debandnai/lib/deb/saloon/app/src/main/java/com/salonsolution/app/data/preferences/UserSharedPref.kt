package com.salonsolution.app.data.preferences

import android.content.Context
import com.google.gson.GsonBuilder
import com.salonsolution.app.data.model.LoginModel
import com.salonsolution.app.data.model.ProfileDetailsModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserSharedPref @Inject constructor (@ApplicationContext val context: Context) {

    companion object{
        private const val USER_DATA = "user_data"
        private const val USER_TOKEN = "token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val LOGGED_IN_DATA= "logged_in_data"
        private const val IS_ACTIVE= "is_active"
    }

    private var userData = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)

    /*............................set Get UserToken data..................................*/
    private fun setUserToken(userToken: String?) {
        if (!userToken.isNullOrEmpty()) {
            val editor = userData.edit()
            editor.putString(USER_TOKEN, userToken)
            editor.apply()
        }

    }

    fun getUserToken(): String {
        return userData.getString(USER_TOKEN, "") ?: ""
    }

    /*............................set Get RefreshToken data..................................*/
    private fun setRefreshToken(refreshToken: String?) {
        if (!refreshToken.isNullOrEmpty()) {
            val editor = userData.edit()
            editor.putString(REFRESH_TOKEN,refreshToken )
            editor.apply()
        }

    }

    fun getRefreshToken(): String {
        return userData.getString(REFRESH_TOKEN, "") ?: ""
    }

    /*............................set Get Is user verified..................................*/
    fun setIsActive(isActive: Int?) {
        isActive?.let {
            val editor = userData.edit()
            editor.putInt(IS_ACTIVE,it )
            editor.apply()
        }

    }

    fun getIsActive(): Int {
        return userData.getInt(IS_ACTIVE,0)
    }


    fun setLoginData(data: LoginModel?) {
        data?.let {
            //user token
            setUserToken(it.token)
            //refresh token
            setRefreshToken(it.refreshToken)
            //user verified or not
            setIsActive(it.isActive)
            //save user details
            val json: String = GsonBuilder()
                .create()
                .toJson(it)
            val editor = userData.edit()
            editor.putString(LOGGED_IN_DATA, json)
            editor.apply()
        }
    }

    fun getLoginData(): LoginModel {
        val json = userData.getString(LOGGED_IN_DATA,"{}")
        return GsonBuilder()
            .create()
            .fromJson(json, LoginModel::class.java)
    }

    fun setProfileDetails(profileDetailsModel: ProfileDetailsModel?){
        profileDetailsModel?.let {
            val data =  getLoginData()
            data.email = it.email
            data.customerFName = it.firstName
            data.customerLName = it.lastName
            data.phone = it.phone
            data.countryCode = it.countryCode
            data.address1 = it.address1
            data.address2 = it.address2
            data.profileImage = it.image
            setLoginData(data)
        }
    }

    fun clearUserData() {
        userData?.edit()?.clear()?.apply()
    }
}