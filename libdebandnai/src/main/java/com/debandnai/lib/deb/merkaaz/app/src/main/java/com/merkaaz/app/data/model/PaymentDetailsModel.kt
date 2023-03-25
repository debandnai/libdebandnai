package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class PaymentDetailsModel(
    @SerializedName("total"           ) var total          : String? = null,
    @SerializedName("delivery_charge" ) var deliveryCharge : String? = null,
    @SerializedName("grand_total"     ) var grandTotal     : String? = null
)
