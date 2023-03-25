package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class LoginRegistrationRepository(private val apiInterface: ApiInterface) : BaseRepository() {

    //log out
    suspend fun logout(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.logoutApi(jsonObject, token, "application/json")
    }
}