package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class DeisStudentSchoolDetailsRepository(private val apiInterface: ApiInterface):BaseRepository() {
    //edit student
    suspend fun editStudent(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.editStudentResponseApi(jsonObject, token, "application/json")
    }
}