package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class ProductDetailsModel(
    @SerializedName("product_id") var productId: Int? = null,
    @SerializedName("product_name") var productName: String? = null,
    @SerializedName("product_desc") var productDesc: String? = null,
    @SerializedName("product_term") var productTerm: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("in_cart") var inCart: Int? = null, //1 -> Added in cart; 0 -> Not in cart
    @SerializedName("images") var images: ArrayList<ProductImages> = arrayListOf()
)


data class ProductImages(

    @SerializedName("image_id") var imageId: Int? = null,
    @SerializedName("image_link") var imageLink: String? = null,
    @SerializedName("is_default") var isDefault: String? = null

)