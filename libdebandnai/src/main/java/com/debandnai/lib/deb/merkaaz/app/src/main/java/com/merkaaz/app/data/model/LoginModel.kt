package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("token") var token: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("vendor_name") var vendorName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("is_active") var isActive: Int? = -1,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("referrence_point") var referencePoint: String? = null,
    @SerializedName("shop_name") var shopName: String? = null,
    @SerializedName("profile_image") var profileImage: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("tax_id") var taxId: String? = null
)
