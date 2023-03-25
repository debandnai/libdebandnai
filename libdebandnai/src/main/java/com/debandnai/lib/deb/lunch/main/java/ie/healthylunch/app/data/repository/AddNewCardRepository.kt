package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class AddNewCardRepository(
    private val apiInterface: ApiInterface
) : BaseRepository() {

    //initiate payment
    suspend fun initiatePaymentIntent(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.initiatePaymentIntentApi(jsonObject, token, "application/json")
    }

    //update Stripe Balance
    suspend fun updateStripeBalance(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.updateStripeBalanceApi(jsonObject, token, "application/json")
    }
}