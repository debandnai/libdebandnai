package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class CartCountModel(@SerializedName("prod_count")
                          val prodCount: Int? = 0)