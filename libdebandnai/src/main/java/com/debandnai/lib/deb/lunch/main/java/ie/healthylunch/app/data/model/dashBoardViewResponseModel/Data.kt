package ie.healthylunch.app.data.model.dashBoardViewResponseModel

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("dashboard_list") val dashboardList: List<DashboardListItem>? = null,
    @SerializedName("date_30") val date_30: Date_30? = null,
    @SerializedName("holidays") val holidaysList: ArrayList<String>? = null
)