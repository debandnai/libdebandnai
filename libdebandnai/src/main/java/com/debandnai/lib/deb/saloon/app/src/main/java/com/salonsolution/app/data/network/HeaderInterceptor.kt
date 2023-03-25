package com.salonsolution.app.data.network

import com.salonsolution.app.data.preferences.UserSharedPref
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(private val userSharedPref: UserSharedPref) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        val token = userSharedPref.getUserToken()
        request.addHeader("Authorization", token)
        return chain.proceed(request.build())
    }
}