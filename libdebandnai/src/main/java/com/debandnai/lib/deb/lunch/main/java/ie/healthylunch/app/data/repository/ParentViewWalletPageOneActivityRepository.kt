package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class ParentViewWalletPageOneActivityRepository(private val apiInterface: ApiInterface) :
    BaseRepository() {
    //card list
    suspend fun cardList(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.cardListApi(jsonObject, token, "application/json")
    }

    //Delete Card
    suspend fun deleteCard(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.deleteCardApi(jsonObject, token, "application/json")
    }
}