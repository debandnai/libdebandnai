package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("token") var token: String? = null,
    @SerializedName("refresh_token") var refreshToken: String? = null,
    @SerializedName("customer_fname") var customerFName: String? = null,
    @SerializedName("customer_lname") var customerLName: String? = null,
    @SerializedName("customer_name") var customerName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("is_active") var isActive: Int? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("country_code") var countryCode: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("profile_image") var profileImage: String? = null,
    @SerializedName("country") var country: String? = null
)