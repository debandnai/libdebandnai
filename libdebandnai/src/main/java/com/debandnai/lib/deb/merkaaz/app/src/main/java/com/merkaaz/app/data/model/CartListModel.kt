package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class CartListModel(
    @SerializedName("category_list")
    val categoryList: List<CategoryListItem?>?,
    @SerializedName("cart_total")
    val cartTotal: String? = null,
    @SerializedName("min_order_amount")
    val minimumOrderAmount: String? = null
)

data class CategoryListItem(
    @SerializedName("category_name")
    val categoryName: String? = null,
    @SerializedName("category_id")
    val categoryId: String? = null,
    @SerializedName("list_count")
    val listCount: Int? = 0,
    @SerializedName("product_list")
    val productList: List<ProductListItem?>?
)

data class ProductListItem(
    @SerializedName("cart_id")
    val cartId: String? = null,
    @SerializedName("cart_quantity")
    val cartQuantity: Int? = 0,
    @SerializedName("image_link")
    val imageLink: String? = null,
    @SerializedName("total_price")
    val totalPrice: Int? = 0,
    @SerializedName("product_id")
    val productId: String? = null,
    @SerializedName("brand_name")
    val brandName: String? = null,
    @SerializedName("sku")
    val sku: String? = null,
    @SerializedName("product_name")
    val productName: String? = null,
    @SerializedName("variation")
    val variationList: List<VariationDataItem?>? = ArrayList(),
    @SerializedName("brand_id")
    val brandId: String? = null,
    var selectedVariationDataItem: VariationDataItem? = null
)