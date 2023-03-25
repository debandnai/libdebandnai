package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class MenuTemplateRepository(private val apiInterface: ApiInterface) : BaseRepository()  {
    suspend fun studentListRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.studentListApi(jsonObject, token, "application/json")
    }
    suspend fun menuTemplateListRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.menuTemplateResponseApi(jsonObject, token, "application/json")
    }

}