package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class CouponModel(
    @SerializedName("coupon_applied") var couponApplied: String? = null,
    @SerializedName("new_total") var newTotal: String? = null
)
