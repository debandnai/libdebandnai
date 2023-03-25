package ie.healthylunch.app.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import ie.healthylunch.app.data.model.sixDayMenuListModel.DataItem
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Constants {
    companion object {
        const val USER_DETAILS = "USER_DETAILS"
        const val PUSH_MODE = "P"
        const val DEVICE_OS = "And"
        const val y_TAG= "y"
        const val ZERO= 0
        const val ONE= 1
        const val ZERO_POINT_ZERO : String = "0.00"
        const val Y : String = "y"
        const val N : String = "n"
        var LAST_ADDED_STUDENT_NAME: String = ""
        var FROM_NETWORK_ERROR_PAGE: Boolean = false
        var APP_UPDATE_REQUEST: Int = 200
        var ADD_ANOTHER_STUDENT: Boolean = false
        var SELECTED_SCHOOL_POSITION: Int = 0
        var SELECTED_CLASS_POSITION: Int = 0
        var SELECTED_COUNTY_POSITION: Int = 0
        var SELECTED_STUDENT_ID: Int = 0
        var TOKEN: String = ""
        var REFRESH_TOKEN: String = ""
        var STUDENT_ID: Int = -1
        var PARENT_ID: Int = 0
        var CURRENT_DAY: Int = -1
        var ORDER_DATE: String = ""
        var DASHBOARD_ID: Int? = 0
        var DASHBOARD_ID_VALUE:Int=0
        var ORDER_VALUE="order"
        var STUDENT_CODE="ST"
        var TEACHER_CODE="TR"
        var MONDAY="Monday"
        var ST="st"
        var ND="nd"
        var RD="rd"
        var TH="th"
        var STATUS_100: Int = 100
        var STATUS_70 = 70.0f
        var STATUS_35 = 35.0f
        var SCHOOL_TYPE="schoolType"
        var WEDNESDAY="Wednesday"
        var ORDER_PROCESS_DIALOG="ORDER_PROCESS_DIALOG"
        var CALENDAR_DISABLE_TIME: String="CALENDAR_DISABLE_TIME"
        var THURADAY="Thursday"
        var FRIDAY="Friday"
        var TUESDAY="Tuesday"
        var STUDENT_ID_TAG_2="student_id"
        var USER_TYPE_TAG_2="user_type"
        var IS_SUB_MENU="is_sub_menu"
        var HOLIDAY_STATUS:Int=1
        var USER_TYPE: String = ""
        var USER_NAME: String = ""
        var CHECK_REGISTRATION: Boolean = false
        var SELECTED_STUDENT_POSITION: Int = 0
        var STATUS_MINUS_ONE: Int = -1
        var STATUS_MINUS_TWO: Int = -2
        var STATUS_ZERO: Int = 0
        var STATUS_ONE: Int = 1
        var STATUS_TWO: Int = 2
        var STATUS_THREE: Int = 3
        var STATUS_FOUR: Int = 4
        var STATUS_FIVE: Int = 5
        var STATUS_SIX: Int = 6
        var STATUS_SEVEN: Int = 7
        var STATUS_EIGHT: Int = 8
        var STATUS_NINE: Int = 9
        var STATUS_TWELVE: Int = 12
        var STATUS_TEN: Int = 10
        var STATUS_FIFTY: Int = 50
        var STATUS_110: Int = 110
        var ERROR_STATUS_401: Int = 401
        var ERROR_STATUS_307: Int = 307
        var ERROR_STATUS_426: Int = 426
        var TOTAL_NO_OF_ITEMS_PER_PAGE: Int = 10

        var SELECTED_ORDER_POSITION: Int = -1
        var SELECTED_CALENDAR_POSITION: Int = 1
        var SELECTED_ORDER_POSITION_FROM_SIDE_DRAWER = -1

        var HAS_STUDENT_ADDED = false


        var PLAY_SERVICES_RESOLUTION_REQUEST: Int = 2404

        var DELETE_NOTIFICATION_TAG: String ="delete_notification"
        var ORDER_2_TAG ="order2"
        var ORDER_1_TAG ="order1"
        var NO_ORDER_TAG ="no_order"
        var USER_TYPE_TAG ="userType"
        var SAVE_TAG ="save"
        var DELETE_TAG ="delete"
        var CALENDAR_DISABLE_TAG ="calendarDisableTimeTag"
        var CALENDAR_END_DATE_TAG ="calendarEndDateTag"
        var Y_TAG ="y"
        var STUDENT_ID_TAG ="studentId"
        var STUDENT_NAME_TAG ="studentName"
        var SCHOOL_NAME_TAG ="schoolName"
        var SCHOOL_ID_TAG ="schoolId"
        var SCHOOL_ID_TAG_2 ="school_id"
        var CLASS_ID_TAG ="class_id"
        var L_NAME_TAG ="l_name"
        var F_NAME_TAG ="f_name"
        var COMMA_TAG: String =","
        var NOTIFICATION_ID_TAG: String ="notificationid"
        var TRANSACTION_INFO_TAG: String ="transactionInfo"
        var ALL_TAG: String ="all"
        var FIRST_TAG: String =  "first"
        var ROWS_TAG: String =  "rows"
        var HOUR_TAG_16 =  16
        var HOUR_TAG_30 =  30
        var FULL_NAME: String = ""
        var DAY_NAME: String = ""
        var CALENDAR: String = "calendar"
        var WEEK_NAME: String = ""
        var LOGIN_CHECK: String = "login_check"
        const val SATURDAY: String = "Saturday"
        const val SUNDAY: String = "Sunday"
        const val SIDE_DRAWER: String = "sideDrawer"
        const val NO: String = "no"
        const val REPEAT: String = "repeat"
        const val SINGLE: String = "single"
        const val STUDENT: String = "student"
        const val TEACHER: String = "teacher"
        const val YES: String = "yes"
        const val FOR: String = "for"
        const val HAS_ALLERGEN__POPUP_SHOW_TAG: String = "hasAllergenPopupShow"
        const val FOOD_ITEMS: String = "foodItem"
        const val REMOVE_FAVORITES: String = "remove_favorites"
        const val FAV_ORDER_FAVORITES: String = "FAV_ORDER_FAVORITES"
        const val ORDER_CLEAR: String = "order_clear"
        const val CALENDAR_OFF: String = "calendar_off"
        const val RED: String = "Red"
        const val GREEN: String = "Green"
        const val FROM_TAG: String = "from"
        const val CHANGE_PASSWORD: String = "CHANGE_PASSWORD"
        const val PAGE_NAME_TAG: String = "pagename"
        const val ABOUT_US_TAG: String = "about-us"
        const val IS_CALENDAR_SESSION: String = "isCalenderSession"
        const val DASHBOARD_ORDER: String = "dashboardOrder"
        const val ORANGE: String = "Orange"
        const val remove_product_mayallergen: String = "remove_product_mayallergen"
        val hourDateFormat = SimpleDateFormat("HH", Locale.ENGLISH)
        val dayFormat=SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayFormatYearMonthDay=SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var dateMonthYearFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val hourMinuteFormat=SimpleDateFormat("HH:mm", Locale.ENGLISH)

//        @RequiresApi(Build.VERSION_CODES.O)
//        val timeFormatDisable = DateTimeFormatter.ofPattern("HH:mm")
        val dayFormatDay=SimpleDateFormat("dd", Locale.ENGLISH)
        val dayFormatyear=SimpleDateFormat("yyyy", Locale.ENGLISH)
        val dayFormatMonth=SimpleDateFormat("MM", Locale.ENGLISH)
        val timeZone=TimeZone.getTimeZone("Europe/Dublin")
        //Api Name
        const val ABOUT_US = "ABOUT_US"
        const val EMAIL_TAG = "email"
        const val otp_purpose = "otp_purpose"
        const val FNAME_TAG = "f_name"
        const val LNAME_TAG = "l_name"
        const val PHONE_TAG = "phone"
        const val PRIVACY_POLICY = "PRIVACY_POLICY"
        const val CHECK_EMAIL_EXIST = "CHECK_EMAIL_EXIST"
        const val LOGOUT_RESPONSE = "LOGOUT_RESPONSE"
        const val PARENT_CHANGE_PASSWORD = "PARENT_CHANGE_PASSWORD"
        const val TERMS_OF_USE = "TERMS_OF_USE"
        const val PARENT_DETAILS = "PARENT_DETAILS"
        const val PARENT_DETAILS_EDIT_PROFILE = "PARENT_DETAILS_EDIT_PROFILE"
        const val NOTIFICATION_SETTINGS_UPDATE = "NOTIFICATION_SETTINGS_UPDATE"
        const val NOTIFICATION_SETTINGS_LIST = "NOTIFICATION_SETTINGS_LIST"
        const val NOTIFICATION_LIST = "NOTIFICATION_LIST"
        const val DELETE_NOTIFICATION = "DELETE_NOTIFICATION"
        const val PRODUCT_LIST_BY_MENU_TEMPLATE = "PRODUCT_LIST_BY_MENU_TEMPLATE"
        const val CALORIE_METER = "CALORIE_METER"
        const val SAVE_MENU_FOR_SINGLE_DAY = "SAVE_MENU_FOR_SINGLE_DAY"
        const val SINGLE_DAY_ORDER_DATE_DISPLAY = "SINGLE_DAY_ORDER_DATE_DISPLAY"
        const val STUDENT_ALLOWED_ORDER = "STUDENT_ALLOWED_ORDER"
        const val DELETE_STUDENT = "DELETE_STUDENT"
        const val ALLERGEN_COUNT = "ALLERGEN_COUNT"
        const val MENU_TEMPLATE_LIST = "MENU_TEMPLATE_LIST"
        const val FAVORITES_LIST = "FAVORITES_LIST"
        const val DASHBOARD = "DASHBOARD"
        const val STUDENT_LIST = "STUDENT_LIST"
        const val STUDENT_DETAILS = "STUDENT_DETAILS"
        const val EDIT_STUDENT = "EDIT_STUDENT"
        const val FEEDBACK_SUBMIT = "FEEDBACK_SUBMIT"
        var EDIT_ALLERGEN="EDIT_ALLERGEN"
        var NEW_STUDENT="NEW_STUDENT"
        const val PAGE = "page"
        const val STUDENT_NAME = "studentName"

        //const val FEEDBACK_LIST = "FEEDBACK_LIST"
        const val EDIT_PROFILE = "EDIT_PROFILE"
        const val SEND_OTP = "SEND_OTP"
        const val PARENT_EMAIL_VERIFICATION = "PARENT_EMAIL_VERIFICATION"
        const val RESET_PASSWORD = "RESET_PASSWORD"
        const val SCHOOL_LIST = "SCHOOL_LIST"
        const val COUNTRY_LIST = "COUNTRY_LIST"
        const val SCHOOL_CLASS_LIST = "SCHOOL_CLASS_LIST"
        const val ADD_NEW_STUDENT = "ADD_NEW_STUDENT"
        const val ADD_NEW_STUDENT_DEIS = "ADD_NEW_STUDENT_DEIS"
        const val ALLERGEN_LIST = "ALLERGEN_LIST"
        const val NUTRITIONAL_CONSIDERATION = "Nutritional_Consideration"
        const val CULTURAL_CONSIDERATION = "CulturalConsideration"
        const val SAVE_ALLERGEN = "SAVE_ALLERGEN"
        const val SAVE_ALLERGENS_LIST = "SAVE_ALLERGENS_LIST"
        const val NEW_PARENT_EMAIL = "NEW_PARENT_EMAIL"
        const val HOLIDAY_LIST = "HOLIDAY_LIST"
        const val DELETE_HOLIDAY = "DELETE_HOLIDAY"
        const val SAVE_HOLIDAY = "SAVE_HOLIDAY"
        const val SAVE_HOLIDAY_SESSION = "SAVE_HOLIDAY_SESSION"
        const val QUICK_VIEW_DAY = "QUICK_VIEW_DAY"
        const val CLEAR_ORDER = "CLEAR_ORDER"
        const val ADD_FAVORITES = "ADD_FAVORITES"
        const val FAVORITES_ACTIVITY = "FAVORITES_ACTIVITY"
        const val NEXT_DAY_ORDER = "NEXT_DAY_ORDER"
        const val NOTIFICATION_COUNT_API = "NOTIFICATION_COUNT_API"
        const val SIX_DAY_MENU_LIST = "SIX_DAY_MENU_LIST"
        const val SIX_DAY_MENU_CALORIE_LIST = "SIX_DAY_MENU_CALORIE_LIST"
        const val DASHBOARD_BOTTOM_CALENDER = "DASHBOARD_BOTTOM_CALENDER"
        const val CARD_DETAILS = "CARD_DETAILS"
        const val DELETE_CARD = "DELETE_CARD"
        const val WALLET_MANUAL_TOP_UP = "WALLET_MANUAL_TOP_UP"
        const val INITIATE_PAYMENT = "INITIATE_PAYMENT"
        const val UPDATE_STRIPE_BALANCE = "UPDATE_STRIPE_BALANCE"
        const val TRANSACTION_LIST = "TRANSACTION_LIST"
        const val COUPON_VALIDATION = "COUPON_VALIDATION"
        const val REMOVE_PRODUCTS_HAVING_MAY_ALLERGEN = "REMOVE_PRODUCTS_HAVING_MAY_ALLERGEN"
        const val NOTIFICATION_COUNT_INCREASE = "NOTIFICATION_COUNT_INCREASE"
        const val CAMPAIGN_ID = "CAMPAIGN_ID"
        const val NOTIFICATION_TYPE = "NOTIFICATION_TYPE"
        const val FROM = "FROM"
        const val DEIS = "deis"
        const val PRIVATE = "private"
        const val GROUP_ID = "group_id"
        const val JOIN_GROUP = "join_group"
        const val VALUE = "Value"
        var CURRENT_ORDER_DAY = ""
        const val NOTIFICATION = "NOTIFICATION"
        const val PRODUCT = "ProductActivity"
        var ORDER_LIST: List<DataItem> = ArrayList()


        @SuppressLint("ConstantLocale")
        val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        @SuppressLint("ConstantLocale")
        val formatDay: DateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    }


}