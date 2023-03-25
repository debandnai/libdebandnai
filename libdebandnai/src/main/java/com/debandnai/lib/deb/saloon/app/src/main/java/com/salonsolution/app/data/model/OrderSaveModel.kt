package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class OrderSaveModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("order_id") var orderId: String? = null,
)
