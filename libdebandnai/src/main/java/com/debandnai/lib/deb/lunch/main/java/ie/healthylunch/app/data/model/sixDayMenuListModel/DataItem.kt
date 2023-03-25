package ie.healthylunch.app.data.model.sixDayMenuListModel

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("date") val date: String? = null,
    @SerializedName("menu_template_id") val menuTemplateId: String? = null,
    @SerializedName("center_value") val centerValue: String? = null,
    @SerializedName("total_calories") val totalCalories: Int? = 0,
    @SerializedName("respective_total_calories") var respectiveTotalCalories: Int? = 0,
    @SerializedName("menu_template_discount_price") val menuTemplateDiscountPrice: Int? = 0,
    @SerializedName("totalprice") val totalprice: String? = null,
    @SerializedName("respective_total_sugar") var respectiveTotalSugar: Int? = 0,
    @SerializedName("day_off") val dayOff: String? = null,
    @SerializedName("weekday") val weekday: String? = null,
    @SerializedName("total_sugar") val totalSugar: Int? = 0,
    @SerializedName("product_name") val productName: List<String?>?,
    @SerializedName("clear_order") val clearOrder: String? = null,
    @SerializedName("menu_template_price") val menuTemplatePrice: String? = null,
    @SerializedName("day_off_msg") val dayOffMsg: String? = null,
    @SerializedName("template_name") val templateName: String? = null,
    @SerializedName("product_aditional_price") val productAditionalPrice: String? = null,
    @SerializedName("calender_off") val calenderOff: String? = null,
    @SerializedName("order_type") val orderType: String? = null,
    @SerializedName("day") val day: String? = null,
    @SerializedName("noon_snooze") val noonSnooze: Int? = 0,
    @SerializedName("next_day_order") val nextDayOrder: NextDayOrder?,
    var order_available: String? = null,
    var menu_available: String? = null
)