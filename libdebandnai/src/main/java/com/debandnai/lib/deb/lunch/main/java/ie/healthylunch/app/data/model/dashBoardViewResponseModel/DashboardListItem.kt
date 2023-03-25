package ie.healthylunch.app.data.model.dashBoardViewResponseModel

import com.google.gson.annotations.SerializedName

data class DashboardListItem(
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("unique_order_id") var unique_order_id: String? = null,
    @SerializedName("sublunch_menu_name") val sublunchMenuName: String? = null,
    @SerializedName("total_calories") val totalCalories: String? = null,
    @SerializedName("day_off") val dayOff: String? = null,
    @SerializedName("order_price") val orderPrice: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    @SerializedName("total_sugar") val totalSugar: String? = null,
    @SerializedName("dashboard_msg") val dashboardMsg: String? = null,
    @SerializedName("day_off_msg") val dayOffMsg: String? = null,
    @SerializedName("order_date") val orderDate: String? = null,
    @SerializedName("order_status") val orderStatus: String? = null,
    @SerializedName("order_type") val orderType: String? = null,
    @SerializedName("holiday_status") val holidayStatus: Int? = 0,
    @SerializedName("order_status_msg") val orderStatusMsg: Int? = 0,
    @SerializedName("center_value") val centerValue: String? = null,
    @SerializedName("dashboard_id") val dashboardId: Int? = 0,
    @SerializedName("noon_snooze") val noonSnooze: Int? = 0,
    @SerializedName("clear_order") val clearOrder: String? = null,
    @SerializedName("day") val day: String? = null,
    @SerializedName("week_name") val weekName: String? = null,
    @SerializedName("favourite_order_id") var favourite_order_id: Int? = 0,
    @SerializedName("ten_per_order") var ten_per_order: String? = null,
    @SerializedName("late_order_time") var late_order_time: String? = null,
    @SerializedName("late_order_percent") var late_order_percent: String? = null,
    var isHoliday: Boolean =false,
    var isRepeatOrderAndHasOrder: Boolean =false
)