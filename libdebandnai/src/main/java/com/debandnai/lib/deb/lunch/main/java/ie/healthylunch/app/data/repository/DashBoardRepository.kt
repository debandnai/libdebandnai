package ie.healthylunch.app.data.repository

import com.google.gson.JsonObject
import ie.healthylunch.app.data.network.ApiInterface

class DashBoardRepository(private val apiInterface: ApiInterface) : BaseRepository() {


    suspend fun studentList(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.studentListApi(jsonObject, token, "application/json")
    }


    suspend fun parentDetails(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.parentDetailsResponseApi(jsonObject, token, "application/json")
    }

    suspend fun dashboardCalorieMeterSixDay(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.dashboardCalorieMeterSixDayNewApi(jsonObject, token, "application/json")
    }

    suspend fun notificationCount(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.notificationCountResponseApi(jsonObject, token, "application/json")
    }


    //Holiday List
    suspend fun sixDayMenuList(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.sixDayMenuListResponseApi(jsonObject, token, "application/json")
    }

    //Bottom Calender
    suspend fun dashBoardBottomCalender(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.dashBoardBottomCalenderResponseApi(jsonObject, token, "application/json")
    }


    //clear Order
    suspend fun clearOrder(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.clearOrderResponseApi(jsonObject, token, "application/json")
    }

    //Favourite Orders Add
    suspend fun favouriteOrdersAdd(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.favouriteOrdersAddApi(jsonObject, token, "application/json")
    }

 //Favourite Orders Remove
    suspend fun favouriteOrdersRemove(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.favouriteOrdersRemoveApi(jsonObject, token, "application/json")
    }



    //replaced Order For Next Week SameDay
    suspend fun replacedOrderForNextWeekSameDayApi(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.replacedOrderForNextWeekSameDayApi(jsonObject, token, "application/json")
    }

    //dashboard Bottom Calendar New Api
    suspend fun dashboardBottomCalendarNewApi(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.dashboardBottomCalendarNewApi(jsonObject, token, "application/json")
    }

    //dashBoard View Api
    suspend fun dashBoardViewApi(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.dashBoardViewApi(jsonObject, token, "application/json")
    }
    //dashBoard Current Order Api
    suspend fun viewDashboardCurrentOrderApi(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.viewDashboardCurrentOrder(jsonObject, token, "application/json")
    }

    suspend fun favoritesListRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.favoritesListApi(jsonObject, token, "application/json")
    }

    //Save Holiday
    suspend fun saveHoliday(
        jsonObject: JsonObject,
        token: String
    ) = safeApiCall {
        apiInterface.holidaySaveApi(jsonObject, token, "application/json")
    }

}