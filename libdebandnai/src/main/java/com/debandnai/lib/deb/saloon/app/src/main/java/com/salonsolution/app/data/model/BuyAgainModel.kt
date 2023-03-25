package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class BuyAgainModel(
    @SerializedName("buy_status") var buyStatus: Int? = null
)
