package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class DeisStudentUniqueCodeRepository(private val apiInterface: ApiInterface):BaseRepository() {

    //deis student add
    suspend fun deisStudentAdd(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.deisStudentAddApi(jsonObject, token, "application/json")
    }
}