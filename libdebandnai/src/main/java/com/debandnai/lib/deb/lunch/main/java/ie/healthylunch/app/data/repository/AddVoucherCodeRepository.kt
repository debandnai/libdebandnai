package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class AddVoucherCodeRepository(
    private val apiInterface: ApiInterface
) : BaseRepository() {

    //card list
    suspend fun cardList(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.cardListApi(jsonObject, token, "application/json")
    }

    //card list
    suspend fun couponCodeValidation(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.couponCodeValidationApi(jsonObject, token, "application/json")
    }
}