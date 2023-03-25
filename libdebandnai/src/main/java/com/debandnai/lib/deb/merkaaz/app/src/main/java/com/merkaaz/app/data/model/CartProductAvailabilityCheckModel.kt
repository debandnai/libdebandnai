package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class CartProductAvailabilityCheckModel(
    @SerializedName("unavailable_products") val unavailableProducts: String? = null,
    @SerializedName("new_cart_total") val newCartTotal: String? = null,
    @SerializedName("place_status") val placeStatus: Int? = 0,
    @SerializedName("old_cart_total") val oldCartTotal: String? = null,
    @SerializedName("min_order_value") val minOrderValue: String? = null
)