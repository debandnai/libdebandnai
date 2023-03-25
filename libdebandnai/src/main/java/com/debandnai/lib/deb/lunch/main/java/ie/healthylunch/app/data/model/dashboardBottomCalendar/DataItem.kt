package ie.healthylunch.app.data.model.dashboardBottomCalendar

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("total_kaloree")
    val totalKaloree: Int = 0,
    @SerializedName("menu_available")
    val menuAvailable: Int = 0,
    @SerializedName("totalproductadtionalprice")
    val totalproductadtionalprice: String = "",
    @SerializedName("totalprice")
    val totalprice: String = "",
    @SerializedName("weekday")
    val weekday: String = "",
    @SerializedName("day")
    val day: String = "",
    @SerializedName("full_date")
    val fullDate: String = "",
    @SerializedName("order_available")
    val orderAvailable: Int = 0,
    @SerializedName("sugar")
    val sugar: Int = 0,
    @SerializedName("total_sugar")
    val totalSugar: Int = 0,
    @SerializedName("kaloree")
    val kaloree: Int = 0
)
