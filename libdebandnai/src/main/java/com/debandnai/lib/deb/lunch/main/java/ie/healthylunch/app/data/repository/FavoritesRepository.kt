package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class FavoritesRepository(private val apiInterface: ApiInterface) : BaseRepository()  {
    suspend fun studentListRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.studentListApi(jsonObject, token, "application/json")
    }

    suspend fun favoritesListRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.favoritesListApi(jsonObject, token, "application/json")
    }

    //Favourite Orders Remove
    suspend fun favouriteOrdersRemove(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.favouriteOrdersRemoveApi(jsonObject, token, "application/json")
    }


}