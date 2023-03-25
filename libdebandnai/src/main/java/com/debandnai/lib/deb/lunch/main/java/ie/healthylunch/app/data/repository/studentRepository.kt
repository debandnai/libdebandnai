package ie.healthylunch.app.data.repository

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.saveAllergenModel.SaveAllergensResponse
import ie.healthylunch.app.data.model.updateSettingsModel.NotificationSettingsUpdateResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.utils.SingleLiveEvent

class StudentRepository(private val apiInterface: ApiInterface) : BaseRepository() {
    suspend fun countryList(
            jsonObject: JsonObject,
            token: String
    ) = safeApiCall {
        apiInterface.countryApi(jsonObject, token, "application/json")
    }


    suspend fun schoolList(
            jsonObject: JsonObject,
            token: String
    ) = safeApiCall {
        apiInterface.schoolListApi(jsonObject, token, "application/json")
    }


    suspend fun studentClassList(
            jsonObject: JsonObject,
            token: String
    ) = safeApiCall {
        apiInterface.studentClassApi(jsonObject, token, "application/json")
    }

    suspend fun addNewStudentApi(
            jsonObject: JsonObject,
            token: String
    ) = safeApiCall {
        apiInterface.addNewStudentApi(jsonObject, token, "application/json")
    }

    suspend fun allergenList(
            jsonObject: JsonObject,
            token: String
    ) = safeApiCall {
        apiInterface.allergenListApi(jsonObject, token, "application/json")
    }

    suspend fun saveAllergensApi(
            jsonObject: JsonObject,
            token: String
    ) = safeApiCall {
        apiInterface.saveAllergensApi(jsonObject, token, "application/json")
    }

    suspend fun studentListRepository(
            jsonObject: JsonObject,
            token: String

    ) = safeApiCall {
        apiInterface.studentListApi(jsonObject, token, "application/json")
    }
    suspend fun studentDetails(
            jsonObject: JsonObject,
            token: String

    ) = safeApiCall {
        apiInterface.studentDetailsApi(jsonObject, token, "application/json")
    }

    suspend fun deleteStudentApi(
            jsonObject: JsonObject,
            token: String

    ) = safeApiCall {
        apiInterface.deleteStudentApi(jsonObject, token, "application/json")
    }
    suspend fun editStudent(
            jsonObject: JsonObject,
            token: String

    ) = safeApiCall {
        apiInterface.editStudentResponseApi(jsonObject, token, "application/json")
    }

    suspend fun logout(
            jsonObject: JsonObject,
            token: String

    ) = safeApiCall {
        apiInterface.logoutApi(jsonObject, token, "application/json")
    }
    // Student  list
    suspend fun studentList(
            jsonObject: JsonObject,
            token: String

    ) = safeApiCall {
        apiInterface.studentListApi(jsonObject, token, "application/json")
    }

    //Save Allergen
    val _saveAllergenListResponse = SingleLiveEvent<Resource<SaveAllergensResponse>?>()
    val saveAllergenListResponse : LiveData<Resource<SaveAllergensResponse>?>
        get() = _saveAllergenListResponse


    // is student allergen available or not
    suspend fun studentAllergenCountApi(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.studentAllergenCountApi(jsonObject, token)
    }
}
