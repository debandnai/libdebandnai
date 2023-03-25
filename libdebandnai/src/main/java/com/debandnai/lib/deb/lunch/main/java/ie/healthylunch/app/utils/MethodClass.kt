package ie.healthylunch.app.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import ie.healthylunch.app.BuildConfig
import ie.healthylunch.app.R
import ie.healthylunch.app.databinding.CalendarOffDialogBinding
import ie.healthylunch.app.databinding.ErrorPopupBinding
import ie.healthylunch.app.ui.LoginRegistrationActivity
import ie.healthylunch.app.ui.MaintenanceActivity
import ie.healthylunch.app.utils.Constants.Companion.DEVICE_OS
import ie.healthylunch.app.utils.Constants.Companion.PLAY_SERVICES_RESOLUTION_REQUEST
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FIVE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FOUR
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_THREE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.dayFormatYearMonthDay
import ie.healthylunch.app.utils.KeyEncryption.Companion.decrypt
import ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer
import ie.healthylunch.app.utils.customViewPager.HorizontalMarginItemDecoration
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


object MethodClass {

    private var mDialog: Dialog? = null
    fun showProgressDialog(activity: Activity) {
        try {
            if (!activity.isFinishing) {
                if (mDialog?.isShowing == true) {
                    mDialog?.dismiss()
                    Log.e("loader", "showProgressDialog: ")
                }
                mDialog = Dialog(activity)
                mDialog?.setCancelable(false)
                mDialog?.window
                    ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mDialog?.setContentView(R.layout.custom_progress_bar)
                mDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("error_loader", e.message!!)
        }
    }

    fun loaderDialog(ctx : Context) : Dialog{
        val loaderDialog = Dialog(ctx)
        loaderDialog.setCancelable(false)
        loaderDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loaderDialog.setContentView(R.layout.custom_progress_bar)
        return loaderDialog

    }

    fun showProgressDialog(fragment: Fragment) {
        try {
            if (!fragment.isAdded) {
                if (mDialog?.isShowing == true) {
                    mDialog?.dismiss()
                    Log.e("loader", "showProgressDialog: ")
                }
                mDialog = Dialog(fragment.requireContext())
                mDialog?.setCancelable(false)
                mDialog?.window
                    ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mDialog?.setContentView(R.layout.custom_progress_bar)
                mDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("error_loader", e.message!!)
        }
    }

    fun hideProgressDialog(activity: Activity) {
        try {
            if (mDialog != null && !activity.isFinishing) {
                mDialog?.dismiss()
                mDialog = null
                Log.e("hide_progress", "hideProgressDialog: ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideProgressDialog(fragment: Fragment) {
        try {
            if (mDialog != null && fragment.isAdded) {
                mDialog?.dismiss()
                mDialog = null
                Log.e("hide_progress", "hideProgressDialog: ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCommonJsonObject(context: Context): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("device_os", DEVICE_OS)
        jsonObject.addProperty("appversion", BuildConfig.VERSION_NAME)
        //jsonObject.addProperty("device_token", "test")
        jsonObject.addProperty("device_token", UserPreferences.getFirebaseToken(context))
        jsonObject.addProperty("device_name", getDeviceName())
        jsonObject.addProperty("device_model", getDeviceModel())
        jsonObject.addProperty("device_version", getDeviceVersion())
        jsonObject.addProperty("push_mode", Constants.PUSH_MODE)
        jsonObject.addProperty("appname", context.resources.getString(R.string.app_name))
        jsonObject.addProperty("timezone", getTimeZone())
        jsonObject.addProperty("device_uid", deviceUid(context))
        jsonObject.addProperty("isLogin", 0)
        return jsonObject
    }

    /*//get App version
    fun getAppVersion(context: Context): String {
        var version = ""
        try {
           *//* val pInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)*//*
           // version = pInfo.versionName
            version = BuildConfig.VERSION_NAME.toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("getAppVersion: ", version)
        return version
    }*/

    //get Device Name
    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL

        return if (model.startsWith(manufacturer)) {
            Log.e("getDeviceName: ", capitalize(model))
            capitalize(model)
        } else {
            Log.e("getDeviceName: ", capitalize("$manufacturer $model"))
            capitalize("$manufacturer $model")
        }

    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    //get Device Model
    private fun getDeviceModel(): String {
        Log.e("getDeviceModel: ", Build.MODEL)
        return Build.MODEL
    }

    //get TimeZone
    private fun getTimeZone(): String {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Dublin"))
        return TimeZone.getDefault().id
    }

    //get deviceUid
    @SuppressLint("HardwareIds")
    fun deviceUid(context: Context): String {

        //String device_uid = tManager.getDeviceId() != null ? tManager.getDeviceId() : "123";
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    //get Device version
    private fun getDeviceVersion(): String {
        Log.e("getDeviceVersion: ", Build.VERSION.SDK_INT.toString())
        return Build.VERSION.SDK_INT.toString()
    }


    @SuppressLint("ClickableViewAccessibility")
    fun hideKeyboardByClickingOutside(view: View, activity: Activity) {
        //First time hide keyboard
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideSoftKeyboard(activity)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView: View = view.getChildAt(i)
                hideKeyboardByClickingOutside(
                    innerView,
                    activity
                )
            }
        }
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }

    fun compareTwoDate(dateVal1: Date, dateVal2: Date): Boolean {
        val date1: Int = dateVal1.date + dateVal1.month + dateVal1.year
        val date2: Int = dateVal2.date + dateVal2.month + dateVal2.year
        return date1 == date2
    }

    fun compareTwoDate(dateVal1: Date, dateVal2: Date, dateVal3: Date): Boolean {
        val date1: Int = dateVal1.date + dateVal1.month + dateVal1.year
        val date2: Int = dateVal2.date + dateVal2.month + dateVal2.year
        val date3: Int = dateVal3.date + dateVal3.month + dateVal3.year
        return if (date1 == date2)
            date1 == date3
        else
            false
    }

    //check day and return corresponding integer value
    fun getIntegerValueOfDay(day: String?): Int {
        return when (day?.lowercase()) {
            "monday" -> STATUS_ONE
            "tuesday" -> STATUS_TWO
            "wednesday" -> STATUS_THREE
            "thursday" -> STATUS_FOUR
            "friday" -> STATUS_FIVE
            "saturday" -> STATUS_ONE
            "sunday" -> STATUS_ONE
            else -> STATUS_ONE
        }

    }

    fun getDayNameOfIntegerDayValue(day: Int): String {
        return when (day) {
            STATUS_ONE -> "monday"
            STATUS_TWO -> "tuesday"
            STATUS_THREE -> "wednesday"
            STATUS_FOUR -> "thursday"
            STATUS_FIVE -> "friday"

            else -> "monday"
        }

    }


    //check day and return corresponding integer value
    fun getShortFormOfDay(day: String): String {
        return when (day.lowercase()) {
            "monday" -> "mon"
            "tuesday" -> "tue"
            "wednesday" -> "wed"
            "thursday" -> "thu"
            "friday" -> "fri"
            "saturday" -> "sat"
            "sunday" -> "sun"
            else -> "mon"
        }

    }


    fun compareNextDate(dateVal1: Date, dateVal2: Date?, dateVal3: Date): Boolean {
        return compareTwoDate(
            addOneDayWithADAte(dateVal1),
            addOneDayWithADAte(dateVal2!!),
            dateVal3
        )
    }

    private fun addOneDayWithADAte(dateVal1: Date): Date {
        val c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin"))
        c.time = dateVal1
        c.add(Calendar.DATE, 1)
        return c.time
    }

    fun showErrorDialog(activity: Activity, errorString: String, errorCode: Int?) {
        when (errorCode) {
            426 -> {
                //showAppUpdateDialog(activity, errorString)
                val intent = Intent(activity, LoginRegistrationActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("from", "appUpdate")
                intent.putExtra("errorString", errorString)
                activity.startActivity(intent)
            }
            307 -> {
                val intent = Intent(activity, MaintenanceActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
            }
            else -> {
                showResponseErrorDialog(activity, errorString)
            }
        }
    }


    /*private lateinit var appUpdateDialog: Dialog
    private fun showAppUpdateDialog(activity: Activity, errorString: String) {
        appUpdateDialog = Dialog(activity)
        appUpdateDialog.window?.setBackgroundDrawableResource(R.color.transparent)

        appUpdateDialog.setCancelable(false)
        //Fullscreen
        appUpdateDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        appUpdateDialog.setContentView(R.layout.app_update_popup)

        val versionNameTv = appUpdateDialog.findViewById<TextView>(R.id.versionNameTv)
        val versionMessageTv = appUpdateDialog.findViewById<TextView>(R.id.versionMessageTv)
        val updateBtn = appUpdateDialoit.value.response.raws.data.orderDateg.findViewById<Button>(R.id.updateBtn)

        versionNameTv.text = getAppVersion(activity)
        try {
            val jsonObject = JSONObject(errorString)

            versionMessageTv.text =
                jsonObject.getJSONObject("response").getJSONObject("status").getString("msg")
                    ?: activity.resources.getString(R.string.update_msg)
            updateBtn.setOnClickListener {
                val appUpdateManager = AppUpdateManagerFactory.create(activity)
                val appUpdateInfoTask = appUpdateManager.appUpdateInfo

                appUpdateInfoTask.addOnSuccessListener {
                    if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                            AppUpdateType.IMMEDIATE
                        )
                    )
                        appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            it,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            activity,
                            // Include a request code to later monitor this update request.
                            APP_UPDATE_REQUEST
                        )
                }
                appUpdateDialog.dismiss()
            }
            appUpdateDialog.show()
        } catch (e: JSONException) {
            e.printStackTrace()
            versionMessageTv.text = activity.resources.getString(R.string.update_msg)

        }

        appUpdateDialog.show()
    }*/

    private lateinit var responseErrorDialog: Dialog
    private fun showResponseErrorDialog(activity: Activity, errorString: String) {

        responseErrorDialog = Dialog(activity)
        responseErrorDialog.window?.let { window ->
            window.setBackgroundDrawableResource(R.color.transparent)
            window.decorView.setBackgroundResource(android.R.color.transparent)
            window.setDimAmount(0.0f)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val dialogBinding =
            ErrorPopupBinding.inflate(LayoutInflater.from(activity))
        responseErrorDialog.setContentView(dialogBinding.root)
        responseErrorDialog.setCancelable(false)

        dialogBinding.btnOk.setOnClickListener {
            responseErrorDialog.dismiss()
        }

        try {
            val jsonObject = JSONObject(errorString)

            dialogBinding.tvContent.text =
                jsonObject.getJSONObject("response").getJSONObject("raws")
                    .getString("error_message")
                    ?: activity.resources.getString(R.string.something_went_wrong_please)

        } catch (e: JSONException) {
            e.printStackTrace()

            dialogBinding.tvContent.text = activity.resources.getString(R.string.something_went_wrong_please)
        }
        responseErrorDialog.show()
    }

    fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(
                    activity,
                    resultCode,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )?.show()
            }
            googleApiAvailability.makeGooglePlayServicesAvailable(activity)
            return false
        }
        return true
    }


    fun keyDecryptionMethod(enc_stripe_key: String): String {
        var decryptedVal = ""
        try {

            decryptedVal = decrypt(enc_stripe_key).toString()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return substractLastFour(decryptedVal)
    }

    private fun substractLastFour(decryptedVal: String): String {
        return decryptedVal.substring(0, decryptedVal.length - 4)
    }

    fun getFormattedDateTimeForTransactionList(getDateTime: String): String {
        val lastIndex: Int = getDateTime.lastIndexOf(",") // last index of ','

        var b = ""
        if (lastIndex != -1) {
            val secondLast: Int = getDateTime.lastIndexOf(",") // second last index of  ','
            if (secondLast != -1) {
                b = getDateTime.substring(
                    0,
                    lastIndex
                ) + "|" + getDateTime.substring(secondLast)
            }
        }

        val setValue = b.replace("|,", " | ")
        return setValue.replace(",", ", ")
    }


    @Suppress("DEPRECATION")
    fun getForeGroundActivity(context: Context): String {
        var runningClassName = ""
        try {
            // Get the Activity Manager
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            // Get a list of running tasks, we are only interested in the last one,
            // the top most so we give a 1 as parameter so we only get the topmost.
            val getList = activityManager.getRunningTasks(10)
            if (getList.size > 0) {
                val componentName: ComponentName? =
                    activityManager.getRunningTasks(1)[0].topActivity

                runningClassName = componentName?.className.toString()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return runningClassName
    }

    /*fun isToday(comparedDateString: String?): Boolean {

        //current date
        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        var currentDayStr = currentDay.toString()
        var currentMonthStr = currentMonth.toString()
        var currentYearStr = currentYear.toString()

        if (currentDayStr.length == 1)
            currentDayStr = "0$currentDayStr"

        if (currentMonthStr.length == 1)
            currentMonthStr = "0$currentMonthStr"

        val sdf =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        var currentDate: Date? = null
        try {
            currentDate = sdf.parse("$currentYearStr-$currentMonthStr-$currentDayStr")
        } catch (e: java.lang.Exception) {
        }


        //compared date
        val dateArray = comparedDateString?.split("-")?.toTypedArray()

        val getYear = dateArray?.get(0)
        var getMonth = dateArray?.get(1)
        var getDay = dateArray?.get(2)

        if (getDay?.length == 1)
            getDay = "0$getDay"

        if (getMonth?.length == 1)
            getMonth = "0$getMonth"

        var comparedDate: Date? = null
        try {
            comparedDate = sdf.parse("$getYear-$getMonth-$getDay")
        } catch (e: java.lang.Exception) {
        }

        return currentDate == comparedDate
    }

    fun isCurrentDayTwelveNoon(): Boolean {
        val date = Date()
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        dateFormat.format(date)

        return if (dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse("11:59")))
            return true
        else
            false

    }


    fun isNextDay(comparedDateString: String?): Boolean {
        //current date
        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        var currentDayStr = currentDay.toString()
        var currentMonthStr = currentMonth.toString()
        val currentYearStr = currentYear.toString()

        if (currentDayStr.length == 1)
            currentDayStr = "0$currentDayStr"

        if (currentMonthStr.length == 1)
            currentMonthStr = "0$currentMonthStr"

        val sdf =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        var currentDate: Date? = null
        try {
            currentDate = sdf.parse("$currentYearStr-$currentMonthStr-$currentDayStr")
        } catch (e: java.lang.Exception) {
        }


        //compared date
        val dateArray = comparedDateString?.split("-")?.toTypedArray()

        val getYear = dateArray?.get(0)
        var getMonth = dateArray?.get(1)
        var getDay = dateArray?.get(2)

        if (getDay?.length == 1)
            getDay = "0$getDay"

        if (getMonth?.length == 1)
            getMonth = "0$getMonth"

        var comparedDate: Date? = null
        try {
            comparedDate = sdf.parse("$getYear-$getMonth-$getDay")
        } catch (e: java.lang.Exception) {
        }

        val nextDate = currentDate?.let { addOneDayWithADAte(it) }

        return nextDate == comparedDate
    }*/

    fun setUpStudentFinitePagerContainer(view: FinitePagerContainer) {
        view.setOverlapSlider(
            unSelectedItemRotation = 0f,
            unSelectedItemAlpha = 0.6f,
            minScale = 0.5f,
            itemGap = 16f
        )
    }

    private var gson: Gson? = null

    fun getGsonParser(): Gson? {
        if (null == gson) {
            val builder = GsonBuilder()
            gson = builder.create()
        }
        return gson
    }

    fun extract_Int_From_String(dateStr: String): Int {
        return dateStr.replace("[^0-9]".toRegex(), "").toInt()
    }

    /*fun getDateListBetweenTwoDates(startDate: String, endDate: String): MutableList<LocalDate> {
        var start: LocalDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.parse(startDate)
        } else {

        }
        val end: LocalDate = LocalDate.parse(endDate)
        val totalDates: MutableList<LocalDate> = ArrayList()
        while (!start.isAfter(end)) {
            totalDates.add(start)
            start = start.plusDays(1)
        }
        return totalDates
    }*/

//    @SuppressLint("SimpleDateFormat")
//    fun getDateListBetweenTwoDates(startDate: String?, endDate: String?): List<Date> {
//        val dates = ArrayList<Date>()
//        val calendar = Calendar.getInstance()
//
//        var stDate: Date? = null
//        var edDate2: Date? = null
//        if(startDate!=null && endDate!=null) {
//            stDate = dayFormatYearMonthDay.parse(startDate)
//            edDate2 = dayFormatYearMonthDay.parse(endDate)
//            Log.e("dateString1", "$startDate   $stDate")
//
//
//        }
//        val cal1 = Calendar.getInstance()
//
//        date1?.let {
//            cal1.time = it
//        }
//
//        val cal2 = Calendar.getInstance()
//
//        date2?.let {
//            cal2.time = it
//        }
//
//        while (!cal1.after(cal2)) {
//            dates.add(cal1.time)
//            cal1.add(Calendar.DATE, 1)
//        }
//        Log.d("TAG_dates", "getDateListBetweenTwoDates:   cal1 : $cal1  cal2:$cal2")
//        return dates
//    }

    fun getDatesBetween(startDate: String, endDate: String): List<Date> {
        val datesInRange: ArrayList<Date> = ArrayList()
        val calendar = dayFormatYearMonthDay.parse(startDate)?.let { getCalendarWithoutTime(it) }
        val endCalendar = dayFormatYearMonthDay.parse(endDate)?.let { getCalendarWithoutTime(it) }
        // To pick the end date
        endCalendar?.add(Calendar.DATE, 1)
        while (calendar?.before(endCalendar) == true) {
            calendar.add(Calendar.DATE, 1)
            val result = calendar.time
            datesInRange.add(result)
        }
//        Log.d("dateString1", "$startDate   $endDate")
//        Log.d("dateString1", "${datesInRange.toString()}")
        return datesInRange
    }

    private fun getCalendarWithoutTime(date: Date): Calendar {
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        calendar[Calendar.HOUR] = 0
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }

    @SuppressLint("SimpleDateFormat")
    fun isAfterLastOrderDate_yyyy_MM_dd_format(
        relevantDateStr: String,
        lastOrderDateStr: String?
    ): Boolean {
        var isAfter = false
        try {
            //get current date string
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDateStr = sdf.format(Date())

            //get current date
            val currentDate: Date? = sdf.parse(currentDateStr)

            //relevant date
            val relevantDate: Date? =
                sdf.parse(relevantDateStr)


            if (!lastOrderDateStr?.trim().isNullOrEmpty()) {
                //last order date
                val lastOrderDate: Date? =
                    lastOrderDateStr?.let { sdf.parse(it) }
                lastOrderDate?.let {
                    relevantDate?.let {
                        if (relevantDate.after(lastOrderDate))
                            isAfter = true
                    }

                }
            } else {
                currentDate?.let {
                    relevantDate?.let {
                        if (relevantDate.after(currentDate))
                            isAfter = true
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isAfter
    }

    //Function for setup viewPager 2
    fun setupCarousel(
        viewPager: ViewPager2,
        minScale: Float = 0.35f,
        nextItemAlpha: Float = 0.5f
    ) {
        viewPager.offscreenPageLimit = 1

        val nextItemVisiblePx =
            viewPager.context.resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            viewPager.context.resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position

            //get x-y ratio
            val ratio = page.scaleX / page.scaleY
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (minScale * abs(position))
            // Next line scales the item's width. You can remove it if you don't want this effect
            /*if (!(ratio * page.scaleY).isNaN())
                page.scaleX = ratio * page.scaleY
            else
                page.scaleX = 1 - (minScale * abs(position))*/
            page.scaleX = 1 - (minScale * abs(position))

            // If you want a fading effect uncomment the next line:
            page.alpha = nextItemAlpha + (1 - abs(position))
            page.elevation = -abs(position)
        }
        viewPager.setPageTransformer(pageTransformer)

        //// The ItemDecoration gives the current (centered) item horizontal margin so that
        //// it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            viewPager.context,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        viewPager.addItemDecoration(itemDecoration)

    }

    fun rotedImage(image: ImageView) {
        val rotate = RotateAnimation(
            0F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 10000
        rotate.interpolator = LinearInterpolator()
        image.startAnimation(rotate)
    }
    fun checkNetworkConnection(ctx: Context?): Boolean {
        var isConnected = false
        ctx?.let {
            val connectivityManager: ConnectivityManager? =
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetwork = connectivityManager?.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            isConnected = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        return isConnected

    }

    fun showSnackBar(view:View?,msg:String){
        view?.let { view ->
            Snackbar.make(
                view, msg,
                Snackbar.LENGTH_LONG).show()
        }
    }
    
    fun showToast(ctx:Context?,msg:String){
        ctx?.let { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()}
    }
    
    fun calenderOffDialog(ctx : Context){
        val calenderOffPopup = Dialog(ctx)

        calenderOffPopup.window?.let { window ->
            window.setBackgroundDrawableResource(R.color.transparent)
            window.decorView.setBackgroundResource(android.R.color.transparent)
            window.setDimAmount(0.0f)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val dialogBinding =
            CalendarOffDialogBinding.inflate(LayoutInflater.from(ctx))
        calenderOffPopup.setContentView(dialogBinding.root)
        calenderOffPopup.setCancelable(false)

        dialogBinding.btnGoBack.setOnClickListener {
            calenderOffPopup.dismiss()
        }
        calenderOffPopup.show()
    }

    fun TextView.setStatus(string: String, color: Int) {
        this.text = string
        this.setTextColor(color)
    }
}

