package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class CartCountModel(
    @SerializedName("cart_count") var cartCount: Int? = null,
)
