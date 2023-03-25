package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class SaveOrderModel(
    @SerializedName("order_id" ) var orderId : String? = null
)
