package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class ReOrderModel(
    @SerializedName("place_status") var placeStatus: Int? = null,
    @SerializedName("old_cart_total") var oldCartTotal: String? = null,
    @SerializedName("new_cart_total") var newCartTotal: String? = null,
    @SerializedName("unavailable_products") var unavailableProducts: ArrayList<UnavailableProducts> = arrayListOf(),
    @SerializedName("min_order_value") var minOrderValue: String? = null
)

data class UnavailableProducts(

    @SerializedName("product_name") var productName: String? = null,
    @SerializedName("msg") var msg: String? = null

)
