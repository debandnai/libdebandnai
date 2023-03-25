package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface


class CalenderRepository(private val apiInterface: ApiInterface) : BaseRepository() {
    //Student List
    suspend fun studentList(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.studentListApi(jsonObject, token, "application/json")
    }

    //Holiday List
    suspend fun holidayList(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.holidayListApi(jsonObject, token, "application/json")
    }

    //Delete Holiday
    suspend fun deleteHoliday(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.holidayDeleteApi(jsonObject, token, "application/json")
    }

    //Save Holiday
    suspend fun saveHoliday(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.holidaySaveApi(jsonObject, token, "application/json")
    }

    //Save Holiday Session
    suspend fun saveHolidaySession(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.holidaySaveSessionApi(jsonObject, token, "application/json")
    }


}