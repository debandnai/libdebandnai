package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class SiteSettingModel(
    @SerializedName("delivery_charge") val deliveryCharge: String = "",
    @SerializedName("banner_subtitle") val bannerSubtitle: String = "",
    @SerializedName("latitude") val latitude: String = "",
    @SerializedName("pickup_address") val pickupAddress: String = "",
    @SerializedName("banner_title") val bannerTitle: String = "",
    @SerializedName("id") val id: Int = 0,
    @SerializedName("minmum_order_amount") val minimumOrderAmount: String = "",
    @SerializedName("banner_image") val bannerImage: String = "",
    @SerializedName("longitude") val longitude: String = ""
)