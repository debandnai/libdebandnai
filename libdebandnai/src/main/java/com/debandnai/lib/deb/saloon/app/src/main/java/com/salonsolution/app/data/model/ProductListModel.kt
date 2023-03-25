package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class ProductListModel(
    @SerializedName("product_list") var productList: ArrayList<ProductList> = arrayListOf(),
    @SerializedName("total_count") var totalCount: Int? = null
)

data class ProductList(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("product_name") var productName: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("in_cart") var inCart: Int? = null  //1 -> Added in cart; 0 -> Not in cart

)