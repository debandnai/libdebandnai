package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class RefreshTokenRepository(
    private val apiInterface: ApiInterface
) : BaseRepository() {
    //refresh token
    suspend fun refreshToken(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.refreshTokenApi(jsonObject, token, "application/json")
    }

    //For Logout
    suspend fun logout(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.logoutApi(jsonObject, token, "application/json")
    }

    //For user delete account
    suspend fun userDeleteAccount(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.userDeleteAccount(jsonObject, token, "application/json")
    }
}