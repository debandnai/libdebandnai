package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class ProductRepository(private val apiInterface: ApiInterface) : BaseRepository()  {

    //student list
    suspend fun studentListRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.studentListApi(jsonObject, token, "application/json")
    }

    suspend fun productListByMenuTemplateRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.productListByMenuTemplateResponseApi(jsonObject, token, "application/json")
    }
    suspend fun calorieMeterRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.calorieMeterResponseApi(jsonObject, token, "application/json")
    }
    suspend fun saveOrderRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.saveOrderResponseApi(jsonObject, token, "application/json")
    }
    /*suspend fun saveMenuForSingleDayRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.saveMenuForSingleDayResponseApi(jsonObject, token, "application/json")
    }
    */
    suspend fun singleDayOrderDateDisplayRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.singleDayOrderDateDisplayResponseApi(jsonObject, token, "application/json")
    }
    suspend fun studentAllowedOrderRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.studentAllowedOrderResponseApi(jsonObject, token, "application/json")
    }
    suspend fun paymentProcessingRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.paymentProcessingResponseApi(jsonObject, token, "application/json")
    }


    //info details api
    suspend fun productInfoDetails(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.productInfoDetailsApi(jsonObject, token, "application/json")
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