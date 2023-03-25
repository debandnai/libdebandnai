package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

class DeliveryOptionsModel {
    @SerializedName("pickup_address"   ) var pickupAddress   : String? = null
    @SerializedName("delivery_address" ) var deliveryAddress : String? = null

    @SerializedName("total"           ) var total          : String? = null
    @SerializedName("delivery_charge" ) var deliveryCharge : String? = null
    @SerializedName("grand_total"     ) var grandTotal     : String? = null
}