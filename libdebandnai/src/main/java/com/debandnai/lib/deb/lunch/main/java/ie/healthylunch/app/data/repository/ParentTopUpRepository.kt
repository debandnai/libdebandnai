package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class ParentTopUpRepository(
    private val apiInterface: ApiInterface
) : BaseRepository() {

    //Delete Card
    suspend fun deleteCard(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.deleteCardApi(jsonObject, token, "application/json")
    }

    ////Wallet Manual TopUp
    suspend fun walletManualTopUp(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.walletManualTopUpApi(jsonObject, token, "application/json")
    }
}