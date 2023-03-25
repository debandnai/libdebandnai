package com.mass.library_calender

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {
        val dateFormatHourMinuits = SimpleDateFormat("HH:mm")
        val timeZone= TimeZone.getTimeZone("Europe/Dublin")
        val STUDENT_HOLIDAY="markedStudentHoliday"
        val ORDER_DATE="dateArrayList"
        val SCHOOL_HOLIDAY="markedHolidayArrayList"
    }
}