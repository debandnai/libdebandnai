package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class VariationDataItem(
    @SerializedName("unit")var unit: String? = null,
    @SerializedName("quantity")val quantity: Int? = 0,
    @SerializedName("size")var size: Int? = null,

    @SerializedName("discount_price")var discountPrice: String? = null,
    @SerializedName("id")val id: String? = null,
    @SerializedName("cart_id") var cartId: String? = null,
    @SerializedName("discount_percentage")val discountPercentage: String = "",
    @SerializedName("sku")val sku: String? = null,
    @SerializedName("is_discounted")val isDiscounted: String? = null,
    @SerializedName("sell_price")val sellPrice: String? = null,
    @SerializedName("present_in_cart") var presentInCart: String? = null,
    @SerializedName("cart_quantity") var cartQuantity: Int? = 0,
    var isSelected: Boolean = false,
    var variationData_qty: Int = 0,
    var total_qty: Int? = null
)