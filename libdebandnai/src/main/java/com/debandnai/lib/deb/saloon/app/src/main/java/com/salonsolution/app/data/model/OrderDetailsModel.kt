package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class OrderDetailsModel(
    @SerializedName("service_list") var serviceList: ArrayList<DetailsServiceList> = arrayListOf(),
    @SerializedName("total_value") var totalValue: String? = null,
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("order_date") var orderDate: String? = null,
    @SerializedName("customer_name") var customerName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("order_status") var orderStatus: Int? = null,
    @SerializedName("canbe_cancelled") var canBeCancelled: Int? = null,
    @SerializedName("coupon_discount") var couponDiscount: String? = null,
    @SerializedName("actual_total") var actualTotal: String? = null
)

data class DetailsServiceList(

    @SerializedName("service_id") var serviceId: Int? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("service_image") var serviceImage: String? = null,
    @SerializedName("booking_date") var bookingDate: String? = null,
    @SerializedName("staff_id") var staffId: Int? = null,
    @SerializedName("staff_name") var staffName: String? = null,
    @SerializedName("service_time") var serviceTime: String? = null,
    @SerializedName("service_cost") var serviceCost: String? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("product_list") var productList: ArrayList<DetailsProductList> = arrayListOf(),
    @SerializedName("food_list") var foodList: ArrayList<DetailsFoodList> = arrayListOf(),
    @SerializedName("can_review") var canReview: Int? = null,
    @SerializedName("is_review_added") var isReviewAdded: Int? = null,

)


data class DetailsFoodList(

    @SerializedName("food_id") var foodId: Int? = null,
    @SerializedName("food_name") var foodName: String? = null,
    @SerializedName("food_desc") var foodDesc: String? = null,
    @SerializedName("food_image") var foodImage: String? = null,
    @SerializedName("food_cost") var foodCost: String? = null,
    @SerializedName("food_qty") var foodQty: Int? = null

)


data class DetailsProductList(

    @SerializedName("product_id") var productId: Int? = null,
    @SerializedName("product_name") var productName: String? = null,
    @SerializedName("product_desc") var productDesc: String? = null,
    @SerializedName("product_image") var productImage: String? = null,
    @SerializedName("product_cost") var productCost: String? = null

)