package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class ChangePasswordActivityRepository (private val apiInterface: ApiInterface) : BaseRepository() {
    suspend fun parentChangePasswordRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.parentChangePasswordResponse(jsonObject, token, "application/json")
    }


}