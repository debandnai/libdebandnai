package com.salonsolution.app.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartListModel(
    @SerializedName("total_value") var totalValue: String? = null,
    @SerializedName("total_service") var totalService: Int? = null,
    @SerializedName("cart_actual_value") var cartActualValue: String? = null,
    @SerializedName("footer_bar") var footerBar: ArrayList<FooterBar> = arrayListOf(),
    @SerializedName("service_list") var cartServiceList: ArrayList<CartServiceList> = arrayListOf()
): Parcelable

@Parcelize
data class CartServiceList(
    @SerializedName("service_cart_id") var serviceCartId: Int? = null,
    @SerializedName("service_id") var serviceId: Int? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("service_image") var serviceImage: String? = null,
    @SerializedName("booking_date") var bookingDate: String? = null,
    @SerializedName("staff_name") var staffName: String? = null,
    @SerializedName("service_time") var serviceTime: String? = null,
    @SerializedName("service_cost") var serviceCost: String? = null,
    @SerializedName("cat_id") var catId: Int? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("staff_id") var staffId: Int? = null,
    @SerializedName("product_list") var productList: ArrayList<CartProductList> = arrayListOf(),
    @SerializedName("food_list") var foodList: ArrayList<CartFoodList> = arrayListOf()

):Parcelable

@Parcelize
data class CartFoodList(

    @SerializedName("food_cart_id") var foodCartId: Int? = null,
    @SerializedName("food_id") var foodId: Int? = null,
    @SerializedName("food_name") var foodName: String? = null,
    @SerializedName("food_image") var foodImage: String? = null,
    @SerializedName("food_cost") var foodCost: String? = null,
    @SerializedName("food_qty") var foodQty: Int? = null

):Parcelable

@Parcelize
data class CartProductList(

    @SerializedName("product_cart_id") var productCartId: Int? = null,
    @SerializedName("product_id") var productId: Int? = null,
    @SerializedName("product_name") var productName: String? = null,
    @SerializedName("product_image") var productImage: String? = null,
    @SerializedName("product_cost") var productCost: String? = null

):Parcelable

@Parcelize
data class FooterBar (
    @SerializedName("category_name" ) var categoryName : String? = null,
    @SerializedName("service_names" ) var serviceNames : String? = null
): Parcelable