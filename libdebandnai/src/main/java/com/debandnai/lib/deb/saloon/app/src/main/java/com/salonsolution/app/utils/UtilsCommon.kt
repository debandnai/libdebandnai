package com.salonsolution.app.utils

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.ui.activity.AuthActivity
import java.text.SimpleDateFormat
import java.util.*

object UtilsCommon {
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()
    }

    fun isValidPhone(phone: String?): Boolean {
        return !phone.isNullOrEmpty() && (phone.length in 9..20)
    }

    fun hideKeyboard(view: View) {
        try {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (_: Exception) {

        }
    }


    fun showKeyboard(view: View) {
        try {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } catch (_: Exception) {

        }
    }

    fun Context.userLogout() {
        UserSharedPref(this).clearUserData()
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        this.startActivity(intent)
       // Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show()
    }

    fun Context.appUpdateRequired() {
        Toast.makeText(this, "App update required", Toast.LENGTH_LONG).show()
    }

    fun calculateProgress(current: String?, total: Int?): Int {
        return try {
            ((current?.toInt() ?: 0) * 100 / (total ?: 0))
        } catch (e: Exception) {
            0
        }
    }

    private const val dateFormat1 = "yyyy-MM-dd"
    fun getFormattedDate(date: Date, localeCode: String): String {
        return try {
            val simpleDateFormat = SimpleDateFormat(dateFormat1, Locale(localeCode))
            simpleDateFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    private const val monthYearFormat = "MMMM yyyy"
    fun getMonthNameWithYear(date: Date, localeCode: String): String {
        return try {
            val simpleDateFormat = SimpleDateFormat(monthYearFormat, Locale(localeCode))
            simpleDateFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    fun Calendar.getZeroTimeDate(localeCode: String): Date {
        return try {
            val calendar = Calendar.getInstance(Locale(localeCode))

            calendar.time = this.time
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            calendar.time
        } catch (e: Exception) {
            this.time
        }
    }

    private const val weekDaysNameFormat = "EEE"
    private const val weekDayFormat = "dd"
    fun getWeekDayWithName(inputDate: String, localeCode: String): Pair<String, String> {
        val weekDay = try {
            val inFormat = SimpleDateFormat(dateFormat1, Locale(localeCode))
            val date = inFormat.parse(inputDate)
            val outFormat = SimpleDateFormat(weekDaysNameFormat, Locale(localeCode))
            val strDay = SimpleDateFormat(weekDayFormat, Locale(localeCode))
            if (date != null) {
                Pair(strDay.format(date), outFormat.format(date))
            } else {
                Pair("", "")
            }

        } catch (e: Exception) {
            Pair("", "")
        }

        return weekDay

    }
    private const val timeFormat = "HH:mm"
    private const val timeFormatWithAmPM = "hh:mma"
    fun getDateFromTime(time:String,localeCode: String):Date{
        val calendar = Calendar.getInstance(Locale(localeCode))
        val inFormat = SimpleDateFormat(timeFormat, Locale(localeCode))
        return try {
            inFormat.parse(time)?:calendar.time
        }catch (e:Exception){
            calendar.time
        }
    }
    fun getTimeFromDate(date:Date,localeCode: String):String?{
        val outFormat = SimpleDateFormat(timeFormat, Locale(localeCode))
        return try {
            outFormat.format(date)
        }catch (e:Exception){
            null
        }
    }

    fun getTimeWithAMPM(date:Date,localeCode: String):String?{
        val outFormat = SimpleDateFormat(timeFormatWithAmPM, Locale(localeCode))
        return try {
            outFormat.format(date)
        }catch (e:Exception){
            null
        }
    }

    fun getCalendarFromDateTimeString(date:String,time:String,localeCode: String): Calendar? {
        val calendar = Calendar.getInstance(Locale(localeCode))
        val inFormat = SimpleDateFormat("$dateFormat1 $timeFormat", Locale(localeCode))
        return try {
            val calTime = inFormat.parse("$date $time")
            if (calTime!=null) {
                calendar.time = calTime
                calendar
            }else{
                null
            }
        }catch (e:Exception){
            null
        }
    }

    fun getNameCharacters(name: String?): String {
        return if(name==null){
            ""
        }else{
            val nameArray =   name.split(' ')
            val char1 = nameArray[0].first()
            val char2 = if(nameArray.size>1) nameArray.last().first() else ""
            "$char1$char2".uppercase()
        }

    }

    fun getNotificationCount(count:Int?):String{
        return if(count!=null && count>0){
            if(count>=100) "99+" else count.toString()
        }else{
            ""
        }
    }

    fun <T> MutableLiveData<T>.clear(){
        this.postValue(null)
    }


}