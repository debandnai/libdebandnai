package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class FoodListModel(
    @SerializedName("food_list") var foodList: ArrayList<FoodList> = arrayListOf(),
    @SerializedName("total_count") var totalCount: Int? = null
)

data class FoodList(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("food_name") var foodName: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("in_cart") var inCart: Int? = null,
    @SerializedName("qty") var qty: Int? = null

)