package ie.healthylunch.app.data.model.sixDayMenuListModel

import com.google.gson.annotations.SerializedName

data class NextDayOrder(
    @SerializedName("current_order_on")
    val currentOrderOn: String? = null,
    @SerializedName("order_status")
    val orderStatus: String? = null,
    @SerializedName("order_replaced")
    val orderReplaced: String? = null,
    @SerializedName("next_order_on")
    val nextOrderOn: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("order_type")
    val orderType: String? = null
)
