package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class FoodDetailsModel(
    @SerializedName("food_id") var foodId: Int? = null,
    @SerializedName("food_name") var foodName: String? = null,
    @SerializedName("food_desc") var foodDesc: String? = null,
    @SerializedName("food_term") var foodTerm: String? = null,
    @SerializedName("food_contain") var foodContain: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("in_cart") var inCart: Int? = null, //1 -> Added in cart; 0 -> Not in cart
    @SerializedName("qty") var qty: Int? = null,
    @SerializedName("images") var images: ArrayList<FoodImages> = arrayListOf()
)

data class FoodImages (

    @SerializedName("image_id"   ) var imageId   : Int?    = null,
    @SerializedName("image_link" ) var imageLink : String? = null,
    @SerializedName("is_default" ) var isDefault : String? = null

)