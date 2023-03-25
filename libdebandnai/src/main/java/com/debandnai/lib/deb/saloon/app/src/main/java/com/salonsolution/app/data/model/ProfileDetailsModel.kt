package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class ProfileDetailsModel(
    @SerializedName("customer_fname") var firstName: String? = null,
    @SerializedName("customer_lname") var lastName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("country_code") var countryCode: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("image") var image: String? = null
)
