package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class ForgotPasswordRepository(private val apiInterface: ApiInterface) : BaseRepository() {
    suspend fun parentRegistrationOtpRepository(
        jsonObject: JsonObject
    ) = safeApiCall {
        apiInterface.parentRegistrationOtpAPI(jsonObject, "application/json")
    }

    suspend fun parentemailverificatiRepository(
        jsonObject: JsonObject
    ) = safeApiCall {
        apiInterface.parentEmailVerificationAPI(jsonObject, "application/json")
    }

    suspend fun ResetPasswordRepository(
        jsonObject: JsonObject
    ) = safeApiCall {
        apiInterface.resetPasswordApi(jsonObject, "application/json")
    }
    suspend fun EditParentEmailRepository(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.editParentEmailApi(jsonObject, token, "application/json")
    }
    //code

}
