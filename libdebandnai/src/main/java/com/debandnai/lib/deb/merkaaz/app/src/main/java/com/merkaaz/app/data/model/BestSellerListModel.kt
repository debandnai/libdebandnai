package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class BestSellerListModel (
    @SerializedName("bestseller_product")
    var bestseller_product: ArrayList<BestsellerProduct> = ArrayList())


class BestsellerProduct {
    @SerializedName("product_id")
    var product_id: String? = null
    @SerializedName("product_id")
    var product_image: String? = null
    @SerializedName("product_id")
    var product_name: String? = null
    @SerializedName("product_id")
    var subcategory_id: String? = null
    @SerializedName("product_id")
    var subcategory_name: String? = null
    @SerializedName("product_id")
    var brand_name: String? = null
    @SerializedName("product_id")
    var variation: ArrayList<Variation>? = null
}







class Variation {
    var id: String? = null
    var sku: String? = null
    var size = 0
    var unit: String? = null
    var sell_price: String? = null
    var discount_price: String? = null
    var discount_percentage: String? = null
    var quantity: String? = null
    var is_discounted: String? = null
}
