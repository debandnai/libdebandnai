package com.salonsolution.app.data.network

import android.util.Log
import com.salonsolution.app.data.preferences.UserSharedPref
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenApi: TokenRefreshApi,
    private val userSharedPref: UserSharedPref,
    private val requestBodyHelper: RequestBodyHelper
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
//            The authenticate() method is called when server returns 401 Unauthorized
            // we need to regeneratetoken
            Log.d("tag","Authenticator: $response")
            try {
                val tokenResponse = tokenApi.regenerateToken(requestBodyHelper.getRefreshTokenRequest(userSharedPref.getRefreshToken()))
                when (val responseState  = ResponseState.create(tokenResponse, userSharedPref)) {

                    is ResponseState.Success -> {
                        Log.d("tag","Authenticator: ${responseState.data?.response?.data}")
                        userSharedPref.setLoginData(responseState.data?.response?.data)
                        response.request.newBuilder()
                            .header("Authorization", "${responseState.data?.response?.data?.token}")
                            .build()
                    }
                    else -> null
                }

            } catch (throwable: Throwable) {
                null
            }

        }
    }
}