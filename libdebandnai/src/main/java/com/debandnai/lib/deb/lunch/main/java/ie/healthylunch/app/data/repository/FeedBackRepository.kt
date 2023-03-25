package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class FeedBackRepository(
    val apiInterface: ApiInterface
) : BaseRepository() {

    //FeedBack List
    suspend fun feedBackList(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.feedBackListApi(jsonObject, token, "application/json")
    }

    //Send FeedBack
    suspend fun sendFeedBack(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.sendFeedBackApi(jsonObject, token, "application/json")
    }
}