package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class LoginRepository(private val apiInterface: ApiInterface) : BaseRepository() {
    suspend fun login(
        jsonObject: JsonObject
    ) = safeApiCall {
        apiInterface.loginResponseAPI(jsonObject,  "application/json")
    }

    //code
}
