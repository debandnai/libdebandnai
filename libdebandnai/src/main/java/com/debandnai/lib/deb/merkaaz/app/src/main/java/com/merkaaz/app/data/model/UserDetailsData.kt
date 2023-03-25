package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class UserDetails(
    @SerializedName("shop_name") var shopName: String? = null,
    @SerializedName("customer_name") var customerName: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("tax_id") var taxId: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("referrence_point") var referencePoint: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("profile_image") var profileImage: String? = null,
    @SerializedName("is_active") var isActive: Int? = null

)
