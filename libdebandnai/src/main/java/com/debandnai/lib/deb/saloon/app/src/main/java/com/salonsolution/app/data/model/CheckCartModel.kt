package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class CheckCartModel(
    @SerializedName("place_order_status") var placeOrderStatus: Int? = null

// 1 -> All Good,
// 2 -> service/product/food price updated,
// 3 -> service/product/food status updated,
// 4 -> service/product/food deleted,
// 5 -> Customer deleted/inactive
)
