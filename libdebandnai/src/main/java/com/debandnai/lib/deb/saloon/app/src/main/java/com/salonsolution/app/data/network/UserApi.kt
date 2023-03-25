package com.salonsolution.app.data.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.BuyAgainModel
import com.salonsolution.app.data.model.*
import com.salonsolution.app.data.model.genericModel.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {

    @POST("services/allcat")
    suspend fun allCategories(@Body categoriesRequest: JsonObject): Response<BaseResponse<CategoriesModel>>

    @POST("services/list")
    suspend fun serviceList(@Body serviceRequest: JsonObject): Response<BaseResponse<ServicesListModel>>

    @POST("services/detail")
    suspend fun serviceDetails(@Body serviceDetailsRequest: JsonObject): Response<BaseResponse<ServiceDetailsModel>>

    @Multipart
    @POST("customer/updateprofile")
    suspend fun updateProfile(
        @Part("customer_fname") firstName: RequestBody,
        @Part("customer_lname") lastName: RequestBody,
        @Part("phone_no") phoneNo: RequestBody,
        @Part("country_code") countryCode: RequestBody,
        @Part("email_id") emailId: RequestBody,
        @Part("address1") address1: RequestBody,
        @Part("address2") address2: RequestBody,
        @Part("device_os") device_os: RequestBody,
        @Part("appversion") appVersion: RequestBody,
        @Part ProfileImage: MultipartBody.Part
    ): Response<BaseResponse<JsonElement>>

    @POST("customer/updatepassword")
    suspend fun updatePassword(@Body updatePasswordRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/profile")
    suspend fun profileDetails(@Body profileDetailsRequest: JsonObject): Response<BaseResponse<ProfileDetailsModel>>

    @POST("product/list")
    suspend fun productList(@Body productListRequest: JsonObject): Response<BaseResponse<ProductListModel>>

    @POST("product/detail")
    suspend fun productDetails(@Body productDetailsRequest: JsonObject): Response<BaseResponse<ProductDetailsModel>>

    @POST("food/list")
    suspend fun foodList(@Body foodListRequest: JsonObject): Response<BaseResponse<FoodListModel>>

    @POST("food/detail")
    suspend fun foodDetails(@Body foodDetailsRequest: JsonObject): Response<BaseResponse<FoodDetailsModel>>

    @POST("staff/list")
    suspend fun staffList(@Body staffListRequest: JsonObject): Response<BaseResponse<StaffListModel>>

    @POST("staff/detail")
    suspend fun staffDetails(@Body staffDetailsRequest: JsonObject): Response<BaseResponse<StaffDetailsModel>>

    @POST("staff/reviewlist")
    suspend fun staffReviewList(@Body staffReviewsRequest: JsonObject): Response<BaseResponse<StaffReviewListModel>>

    @POST("staff/calendar")
    suspend fun staffCalendar(@Body staffCalendarRequest: JsonObject): Response<BaseResponse<StaffCalendarModel>>

    @POST("cart/add")
    suspend fun addToCart(@Body addToCartRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("cart/productadd")
    suspend fun productAdd(@Body productAddRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("cart/foodadd")
    suspend fun foodAdd(@Body foodAddRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("cart/foodupdate")
    suspend fun foodUpdate(@Body foodUpdateRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("cart/list")
    suspend fun cartList(@Body cartListRequest: JsonObject): Response<BaseResponse<CartListModel>>

    @POST("cart/delete")
    suspend fun cartDelete(@Body cartDeleteRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("cart/delnoneed")
    suspend fun doNotNeed(@Body doNotNeedRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("order/checkcart")
    suspend fun checkCart(@Body checkCartRequest: JsonObject): Response<BaseResponse<CheckCartModel>>

    @POST("cart/matchcoupon")
    suspend fun matchCoupon(@Body checkCartRequest: JsonObject): Response<BaseResponse<CouponModel>>

    @POST("order/save")
    suspend fun orderSave(@Body orderSaveRequest: JsonObject): Response<BaseResponse<OrderSaveModel>>

    @POST("customer/logout")
    suspend fun logout(@Body logoutRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/profiledelete")
    suspend fun profileDelete(@Body profileDeleteRequest: JsonObject): Response<BaseResponse<JsonElement>>


    @POST("customer/notificationcount")
    suspend fun notificationCount(@Body notificationCountRequest: JsonObject): Response<BaseResponse<NotificationItemCountModel>>

    @POST("services/search")
    suspend fun serviceSearch(@Body serviceSearchRequest: JsonObject): Response<BaseResponse<ServicesListModel>>

    @POST("order/list")
    suspend fun orderList(@Body orderListRequest: JsonObject): Response<BaseResponse<OrderListModel>>

    @POST("order/cancel")
    suspend fun orderCancel(@Body orderCancelRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("order/buyagain")
    suspend fun buyAgain(@Body buyAgainRequest: JsonObject): Response<BaseResponse<BuyAgainModel>>

    @POST("order/detail")
    suspend fun orderDetails(@Body orderDetailsRequest: JsonObject): Response<BaseResponse<OrderDetailsModel>>

    @POST("customer/reviewadd")
    suspend fun reviewAdd(@Body reviewAddRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("customer/notificationlist")
    suspend fun notificationList(@Body notificationListRequest: JsonObject): Response<BaseResponse<NotificationListModel>>

    @POST("customer/notificationdelete")
    suspend fun notificationDelete(@Body notificationDeleteRequest: JsonObject): Response<BaseResponse<JsonElement>>

    @POST("cart/cartcount")
    suspend fun cartCount(@Body cartCountRequest: JsonObject): Response<BaseResponse<CartCountModel>>

    @POST("customer/cmsdetail")
    suspend fun cmsDetails(@Body cmsDetailsRequest: JsonObject): Response<BaseResponse<CmsModel>>

    @POST("dashboard/upcomingorder")
    suspend fun upcomingOrders(@Body upcomingOrdersRequest: JsonObject): Response<BaseResponse<UpcomingOrderListModel>>

    @POST("dashboard/recentorder")
    suspend fun recentOrders(@Body recentOrdersRequest: JsonObject): Response<BaseResponse<RecentOrderListModel>>

    @POST("dashboard/bookedservice")
    suspend fun bookedServices(@Body bookedServicesRequest: JsonObject): Response<BaseResponse<BookedServiceListModel>>

    @POST("dashboard/recentservice")
    suspend fun recentServices(@Body recentServicesRequest: JsonObject): Response<BaseResponse<RecentServiceListModel>>

    @POST("dashboard/popularservice")
    suspend fun popularServices(@Body popularServicesRequest: JsonObject): Response<BaseResponse<PopularCategoryListModel>>

    @POST("dashboard/latestreview")
    suspend fun latestReviews(@Body latestReviewsRequest: JsonObject): Response<BaseResponse<LatestReviewListModel>>




}