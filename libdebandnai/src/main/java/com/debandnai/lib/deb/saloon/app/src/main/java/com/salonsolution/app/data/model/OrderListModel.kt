package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class OrderListModel(
    @SerializedName("order_list") var orderList: ArrayList<OrderList> = arrayListOf()
)

data class OrderList(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("order_date") var orderDate: String? = null,
    @SerializedName("total_value") var totalValue: String? = null,
    @SerializedName("order_status") var orderStatus: Int? = null,
    @SerializedName("canbe_cancelled") var canbeCancelled: Int? = null,
    @SerializedName("service_list") var serviceList: ArrayList<ServiceList> = arrayListOf()

)

data class ServiceList(
    @SerializedName("service_id") var serviceId: Int? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("service_image") var serviceImage: String? = null,
    @SerializedName("booking_date") var bookingDate: String? = null,
    @SerializedName("staff_name") var staffName: String? = null,
    @SerializedName("service_time") var serviceTime: String? = null,
    @SerializedName("service_cost") var serviceCost: String? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("product_name") var productName: String? = null,
    @SerializedName("product_image") var productImage: String? = null,
    @SerializedName("product_more") var productMore: Int? = null,
    @SerializedName("food_name") var foodName: String? = null,
    @SerializedName("food_image") var foodImage: String? = null,
    @SerializedName("food_more") var foodMore: Int? = null
)