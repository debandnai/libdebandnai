package com.salonsolution.app.data.network

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.JsonObject
import com.salonsolution.app.BuildConfig
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.qualifiers.ApplicationContext

import javax.inject.Inject

class RequestBodyHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSharedPref: UserSharedPref,
    private val appSettingsPref: AppSettingsPref
) {
    companion object {
        private const val TAG = "RequestBody"
    }

    private fun getCommonProperty(): JsonObject {
        val propertyType = JsonObject()
        propertyType.addProperty("device_os", "and")
        propertyType.addProperty("appversion", BuildConfig.VERSION_NAME)
        propertyType.addProperty("lang_name", SettingsUtils.getLanguageValue())
        propertyType.addProperty("currency", appSettingsPref.getCurrency()) // 1-> USD, 2-> KZ


        return propertyType
    }

    fun getLoginRequest(username: String?,password:String?): JsonObject {
        val request = getCommonProperty()
        //val timeZone = TimeZone.getDefault()
       // val timezone = timeZone.id
        val appName = context.getString(R.string.app_name)
        val deviceModel = Build.MODEL
        val deviceName = Build.MODEL
        val deviceVersion = Build.VERSION.RELEASE


        request.addProperty("username", username)
        request.addProperty("password", password)
        request.addProperty("device_version", deviceVersion)
        request.addProperty("device_name", deviceName)
        request.addProperty("device_model", deviceModel)
        request.addProperty("appname", appName)
        request.addProperty("device_token", appSettingsPref.getFCMToken())
        Log.d(TAG, request.toString())
        return request
    }

    fun getRegisterRequest(firstName:String?, lastName:String?, email:String?, phone:String?, countryCode:String?, password:String?): JsonObject{
        val request = getCommonProperty()
        //val timeZone = TimeZone.getDefault()
        // val timezone = timeZone.id
        val appName = context.getString(R.string.app_name)
        val deviceModel = Build.MODEL
        val deviceName = Build.MODEL
        val deviceVersion = Build.VERSION.RELEASE


        request.addProperty("customer_fname", firstName)
        request.addProperty("customer_lname", lastName)
        request.addProperty("country_code", countryCode)
        request.addProperty("email_id", email)
        request.addProperty("phone_no", phone)
        request.addProperty("password", password)
        request.addProperty("device_version", deviceVersion)
        request.addProperty("device_name", deviceName)
        request.addProperty("device_model", deviceModel)
        request.addProperty("appname", appName)
        request.addProperty("device_token", appSettingsPref.getFCMToken())
        Log.d(TAG, request.toString())
        return request
    }

    fun getOtpRequest(type:String?, email:String?, otp:String?):JsonObject{
        val request = getCommonProperty()
        request.addProperty("type", type)
        request.addProperty("email", email)
        request.addProperty("otp", otp)
        Log.d(TAG, request.toString())
        return request
    }

    fun getResendOtpRequest(email:String?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("email", email)
        Log.d(TAG, request.toString())
        return request
    }

    fun getForgotPasswordRequest(email:String?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("email", email)
        Log.d(TAG, request.toString())
        return request
    }

    fun getResetPasswordRequest(email: String?, password:String?):JsonObject{
        val request = getCommonProperty()
        request.addProperty("email", email)
        request.addProperty("new_password", password)
        request.addProperty("confirm_password", password)
        Log.d(TAG, request.toString())
        return request
    }

    fun getAllCategoriesRequest():JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }

    fun getServiceListRequest(sortField: String?,sortOrder:Int?,catId:String?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("rows", TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        request.addProperty("sortField", sortField)
        request.addProperty("sortOrder", sortOrder)
        request.addProperty("cat_id",catId)
        Log.d(TAG, request.toString())
        return request

    }

    fun getServiceDetailsRequest(serviceId:Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id", serviceId)
        Log.d(TAG, request.toString())
        return request
    }

    fun updatePasswordRequest(oldPass:String?, newPass:String?): JsonObject{
        val request = getCommonProperty()

        request.addProperty("old_pass", oldPass)
        request.addProperty("new_pass", newPass)

        Log.d(TAG, request.toString())
        return request
    }

    fun getProfileDetailsRequest():JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }

    fun getRefreshTokenRequest(refreshToken: String):JsonObject{
        val request = getCommonProperty()
        request.addProperty("refreshToken", refreshToken)
        Log.d(TAG, request.toString())
        return request
    }

    fun getProductListRequest(sortField: String?,sortOrder:Int?, serviceId: Int?, staffId: Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("rows", TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        request.addProperty("service_id", serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("sortField", sortField)
        request.addProperty("sortOrder", sortOrder)
        Log.d(TAG, request.toString())
        return request

    }

    fun getProductDetailsRequest(productId:Int?, serviceId: Int?, staffId: Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("product_id", productId)
        request.addProperty("service_id", serviceId)
        request.addProperty("staff_id", staffId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getFoodListRequest(sortField: String?,sortOrder:Int?,serviceId:Int?, staffId: Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("rows", TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        request.addProperty("sortField", sortField)
        request.addProperty("sortOrder", sortOrder)
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        Log.d(TAG, request.toString())
        return request

    }

    fun getFoodDetailsRequest(foodId:Int?,serviceId:Int?, staffId: Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("food_id", foodId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getStaffListRequest(sortField: String?, sortOrder:Int?, serviceId: Int): JsonObject{
        val request = getCommonProperty()
        request.addProperty("rows", TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        request.addProperty("sortField", sortField)
        request.addProperty("sortOrder", sortOrder)
        request.addProperty("service_id",serviceId)
        Log.d(TAG, request.toString())
        return request

    }

    fun getStaffDetailsRequest(serviceId:Int?,staffId:Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getStaffReviewsRequest( serviceId: Int?, staffId: Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("rows", TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        Log.d(TAG, request.toString())
        return request

    }

    fun getStaffCalendarRequest( serviceId: Int?, staffId: Int?, startDate:String?,endDate:String?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("rows", TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("start_date", startDate)
        request.addProperty("end_date", endDate)
        Log.d(TAG, request.toString())
        return request

    }

    fun getAddToCartRequest( serviceId: Int?, staffId: Int?, bookingDate:String?,startTime:String?, endTime:String?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("booking_date", bookingDate)
        request.addProperty("start_time", startTime)
        request.addProperty("end_time", endTime)
        Log.d(TAG, request.toString())
        return request

    }

    fun getProductAddRequest( serviceId: Int?, staffId: Int?, productId:Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("product_id", productId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getFoodAddRequest( serviceId: Int?, staffId: Int?, foodId:Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("food_id", foodId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getFoodUpdateRequest( serviceId: Int?, staffId: Int?, foodId:Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("food_id", foodId)
        request.addProperty("qty_add", 1)
        Log.d(TAG, request.toString())
        return request
    }

    fun getFoodRemovedRequest( serviceId: Int?, staffId: Int?, foodId:Int?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("food_id", foodId)
        request.addProperty("qty_add", 0)
        Log.d(TAG, request.toString())
        return request
    }

    fun getCartListRequest(): JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }

    fun getCartDeleteRequest(type:String?,cartId:Int?): JsonObject{
//        "type":"1/2/3", 1-> Service, 2-> Product, 3 -> Food
        val request = getCommonProperty()
        request.addProperty("type", type)
        request.addProperty("cart_id", cartId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getDoNotNeedRequest(type:String?,serviceId: Int?, staffId: Int?): JsonObject{
        //"type":"1/2/" //1-> Product, 2 -> Food
        val request = getCommonProperty()
        request.addProperty("type", type)
        request.addProperty("service_id",serviceId)
        request.addProperty("staff_id", staffId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getCheckCartRequest(): JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }

    fun getMatchCouponRequest(couponCode:String?, cartTotal:String?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("coupon_code", couponCode)
        request.addProperty("cart_total", cartTotal)
        Log.d(TAG, request.toString())
        return request
    }

    fun getOrderSaveRequest(couponCode:String?): JsonObject{
        val request = getCommonProperty()
        request.addProperty("coupon_code", couponCode)
        request.addProperty("device_token", appSettingsPref.getFCMToken())
        Log.d(TAG, request.toString())
        return request
    }

    fun getLogoutRequest():JsonObject{
        val request = getCommonProperty()
        request.addProperty("device_token", appSettingsPref.getFCMToken())
        Log.d(TAG, request.toString())
        return request
    }
    fun getProfileDeleteRequest():JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }

    fun getNotificationCountRequest():JsonObject{
        val request = getCommonProperty()
        request.addProperty("device_token", appSettingsPref.getFCMToken())
        Log.d(TAG, request.toString())
        return request
    }

    fun getServiceSearchRequest(searchText:String?):JsonObject{
        val request = getCommonProperty()
        request.addProperty("search_term", searchText)
        request.addProperty("rows", TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        Log.d(TAG, request.toString())
        return request
    }
    fun getMyOrderListRequest():JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }

    fun getOrderCancelRequest(orderId: Int?):JsonObject{
        val request = getCommonProperty()
        request.addProperty("order_id", orderId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getBuyAgainRequest(orderId: Int?):JsonObject{
        val request = getCommonProperty()
        request.addProperty("order_id", orderId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getOrderDetailsRequest(orderId: Int?):JsonObject{
        val request = getCommonProperty()
        request.addProperty("order_id", orderId)
        Log.d(TAG, request.toString())
        return request
    }

    fun getReviewAddRequest(orderId: Int?, serviceId: Int?,staffId: Int?,reviewStar: Float?, reviewComment: String?):JsonObject{
        val request = getCommonProperty()
        request.addProperty("order_id", orderId)
        request.addProperty("service_id", serviceId)
        request.addProperty("staff_id", staffId)
        request.addProperty("review_star", reviewStar)
        request.addProperty("review_comment", reviewComment)
        Log.d(TAG, request.toString())
        return request
    }

    fun getNotificationListRequest():JsonObject{
        val request = getCommonProperty()
        request.addProperty("device_token", appSettingsPref.getFCMToken())
//        request.addProperty("device_token", "fU83xqoESDK4ceNX1dJh_t:APA91bHbnKi54LvJmu576K1lhCUJQM9yiF6TwOD731FsyC9ma4DaV54M_vARBgGTb_o2gA0Y8rDPe0JB5eCQqk1UmInIArStko1AJe9o-9JUMVETBD2AJHw7Q0OKuV3hCgkn_adgOUhs")
        Log.d(TAG, request.toString())
        return request
    }
    fun getNotificationDeleteRequest(notificationId:Int?, isAllDelete:Boolean):JsonObject{
        val request = getCommonProperty()
        request.addProperty("notification_id", notificationId?:0) // 0 for all delete
        request.addProperty("del_type", if(isAllDelete) 2 else 1)   //1 -> Single; 2 -> All
        Log.d(TAG, request.toString())
        return request
    }

    fun getDashboardAllRequest():JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }

    fun getCountriesRequest():JsonObject{
        val request = getCommonProperty()
        Log.d(TAG, request.toString())
        return request
    }


    fun getCmsDetailsRequest(cmsId:Int?):JsonObject{
        //1 -> Terms & Conditions; 2 -> Privacy Policy
        val request = getCommonProperty()
        request.addProperty("cms_id", cmsId)
        Log.d(TAG, request.toString())
        return request
    }

}