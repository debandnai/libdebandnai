package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class PrivacyPolicyViewRepository (private val apiInterface: ApiInterface) : BaseRepository() {

    suspend fun privacyPolicyRepository(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.privacyPolicyResponseApi(jsonObject, token, "application/json")
    }
}