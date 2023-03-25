package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class EditParentProfileRepository(private val apiInterface: ApiInterface) : BaseRepository() {
    suspend fun editProfileApiRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.editProfileApi(jsonObject, token, "application/json")
    }

}