package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderListModel(

    @SerializedName("total_count") var totalCount: Int? = null,
    @SerializedName("order_list") var orderList: ArrayList<OrderList> = arrayListOf()
) : Serializable

data class OrderList(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("order_date") var orderDate: String? = null,
    @SerializedName("last_updated") var lastUpdated: String? = null,
    @SerializedName("order_status") var orderStatus: String? = null,
    @SerializedName("order_value") var orderValue: String? = null,
    @SerializedName("delivery_charge") var deliveryCharge: String? = null,
    @SerializedName("total_amount") var totalAmount: String? = null,
    @SerializedName("shipping_type") var shippingType: String? = null,
    @SerializedName("pickup_address") var pickupAddress: String? = null,
    @SerializedName("delivery_address") var deliveryAddress: String? = null,
    @SerializedName("payment_status") var paymentStatus: String? = null,
    @SerializedName("payment_date") var paymentDate: String? = null,
    @SerializedName("product_list") var productList: ArrayList<ProductList> = ArrayList()

) : Serializable


data class ProductList(

    @SerializedName("productid") var productid: Int? = null,
    @SerializedName("sku") var sku: String? = null,
    @SerializedName("product_name") var productName: String? = null,
    @SerializedName("product_image") var productImage: String? = null,
    @SerializedName("brand_name") var brandName: String? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("size") var qtuSize: String? = null,
    @SerializedName("sell_price") var sellPrice: String? = null

) : Serializable