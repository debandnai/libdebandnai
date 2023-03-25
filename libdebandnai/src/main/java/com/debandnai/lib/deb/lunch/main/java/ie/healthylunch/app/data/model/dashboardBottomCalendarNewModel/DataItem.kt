package ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("end_date")
    val endDate: String? = "",
    @SerializedName("order_date")
    var orderDate: List<String>?=null,
    @SerializedName("pending_order_date")
    var pendingOrderDate: List<String>?=null,
    @SerializedName("free_order_date")
    var freeOrderDate: String? = "",
    @SerializedName("start_date")
    val startDate: String? = "",
    @SerializedName("last_order_date")
    val lastOrderDate: String? = null,
    var calendarItemList: List<CalendarItem>
)