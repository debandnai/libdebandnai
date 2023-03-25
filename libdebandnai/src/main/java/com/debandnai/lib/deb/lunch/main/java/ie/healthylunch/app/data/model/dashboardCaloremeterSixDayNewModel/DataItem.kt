package ie.healthylunch.app.data.model.dashboardCaloremeterSixDayNewModel

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("date") val date: String? = null,
    @SerializedName("total_kaloree") val totalKaloree: String? = null,
    @SerializedName("menu_available") val menuAvailable: String? = null,
    @SerializedName("totalproductadtionalprice") val totalproductadtionalprice: String? = null,
    @SerializedName("totalprice") val totalprice: String? = null,
    @SerializedName("weekday") val weekday: String? = null,
    @SerializedName("day") val day: String? = null,
    @SerializedName("order_available") val orderAvailable: String? = null,
    @SerializedName("sugar") val sugar: String? = "0",
    @SerializedName("total_sugar") val totalSugar: Int? = 0,
    @SerializedName("kaloree") val kaloree: String? = "0"
)