package ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel

import com.google.gson.annotations.SerializedName

data class OrderDateItem(
    @SerializedName("order_date")
    var orderDate: String = ""
)