package com.merkaaz.app.network

import com.google.gson.JsonObject
import com.merkaaz.app.data.genericmodel.BaseResponse
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.LoginModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetroApi {

//    @POST("user/getphonenumber")
//    suspend fun getlogin(@Body loginobject: JsonObject): Response<JsonobjectModel?>
    @POST("user/getphonenumber")
    suspend fun getlogin(@Body loginobject: JsonObject): Response<BaseResponse<LoginModel>>

    @POST("user/verify-otp")
    suspend fun getotp(@Body otpobject: JsonObject): Response<JsonobjectModel?>

    @POST("user/register")
    suspend fun getregistration(@Body otpobject: JsonObject): Response<JsonobjectModel?>

    @POST("product/bestfeaturedproduct")
    suspend fun getHomeFeaturedProducts(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("category/list")
    suspend fun getcategoryList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("category/list")
    suspend fun getShopByCategoryList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): JsonobjectModel?

     @POST("category/listallsub")
    suspend fun getAllcategoryList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("order/reorder")
    suspend fun getReorder(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>


    @POST("profitloss/detail")
    suspend fun getProfitLossDetailsList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>


    @POST("product/bestfeaturedproduct")
    suspend fun getFeaturedProductsList(@HeaderMap headers : Map<String, String>, @Body jsonObject: JsonObject): JsonobjectModel?



    @POST("product/detail")
    suspend fun productDetail(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>


    @POST("user/logout")
    suspend fun logout(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @Multipart
    @POST("user/updateprofile")
    suspend fun updateUserProfile(
        @HeaderMap headers: Map<String, String>,
        @Part("shop_name") shop_name: RequestBody,
        @Part("customer_name") customer_name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("tax_id") tax_id: RequestBody,
        @Part("lang_name") lang_name: RequestBody,
        @Part("device_os") device_os: RequestBody,
        @Part("appversion") appversion: RequestBody,
        @Part image : MultipartBody.Part
    ): Response<JsonobjectModel?>

    @POST("user/detail")
    suspend fun getUserDetail(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("user/locationupdate")
    suspend fun getLocationUpdate(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("product/list")
    suspend fun getProductList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): JsonobjectModel?

    @POST("product/listall")
    suspend fun getAllProductList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): JsonobjectModel?

    @POST("cart/save")
    suspend fun add_update(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("cart/delete")
    suspend fun delete_item(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("order/deliveryoption")
    suspend fun getDeliveryOptions(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("cart/list")
    suspend fun getCartList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("cart/prodcount")
    suspend fun getCartCount(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>


    @POST("order/save")
    suspend fun saveOrder(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("order/paymentdetails")
    suspend fun getPaymentDetails(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("order/list")
    suspend fun getOrderList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): JsonobjectModel?

    @POST("profitloss/list")
    suspend fun getProfitlossList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): JsonobjectModel?

    @POST("user/notificationlist")
    suspend fun getNotificationList(@HeaderMap headers: Map<String, String>, @Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("user/notificationdelete")
    suspend fun notificationDelete(@HeaderMap headers: Map<String, String>, @Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("order/cartProductAvailabilityCheck")
    suspend fun cartProductAvailabilityCheck(@HeaderMap headers: Map<String, String>, @Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("product/filter")
    suspend fun getFilterList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("product/filterproductcount")
    suspend fun getFilterCount(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("order/moveonprice")
    suspend fun checkMoveonPrice(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("category/dashboardproducts")
    suspend fun dashboardProductList(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("profitloss/save")
    suspend fun updateProfitLossData(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

    @POST("product/search")
    suspend fun getSearchData(@HeaderMap headers: Map<String, String>,@Body jsonObject: JsonObject): Response<JsonobjectModel?>

}