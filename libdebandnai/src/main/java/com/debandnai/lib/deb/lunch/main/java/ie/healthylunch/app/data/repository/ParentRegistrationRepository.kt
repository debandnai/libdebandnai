package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class ParentRegistrationRepository(private val apiInterface: ApiInterface) : BaseRepository() {
    //Parent Registration
    suspend fun parentRegistration(
        jsonObject: JsonObject,

        ) = safeApiCall {
        apiInterface.parentRegistration(jsonObject, "application/json")
    }

    suspend fun checkEmailExistRepository(
        jsonObject: JsonObject
    ) = safeApiCall {
        apiInterface.checkEmailExistApi(jsonObject, "application/json")
    }

    suspend fun parentRegistrationOtp(
        jsonObject: JsonObject
    ) = safeApiCall {
        apiInterface.parentRegistrationOtpAPI(jsonObject, "application/json")
    }

    suspend fun parentEmailVerification(
        jsonObject: JsonObject
    ) = safeApiCall {
        apiInterface.parentEmailVerificationAPI(jsonObject, "application/json")
    }

    suspend fun editParentEmail(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.editParentEmailApi(jsonObject, token, "application/json")
    }

}