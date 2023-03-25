package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class RemoveProductsHavingMayAllergenRepository(
    private val apiInterface: ApiInterface
) : BaseRepository() {

    //Remove Product May Allergen Api
    suspend fun removeProductMayAllergen(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.removeProductMayAllergenApi(jsonObject, token, "application/json")
    }
}