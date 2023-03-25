package com.mass.library_calender

import java.util.*

object CalendarUtils {
    fun isSameMonth(c1: Calendar?, c2: Calendar?): Boolean {
        return if (c1 == null || c2 == null) false else c1[Calendar.ERA] == c2[Calendar.ERA] && c1[Calendar.YEAR] == c2[Calendar.YEAR] && c1[Calendar.MONTH] == c2[Calendar.MONTH]
    }

    /**
     *
     * Checks if a calendar is today.
     *
     * @param calendar the calendar, not altered, not null.
     * @return true if the calendar is today.
     * @throws IllegalArgumentException if the calendar is `null`
     */
    fun isToday(calendar: Calendar?): Boolean {
        return isSameDay(calendar, Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin")))
    }

    private fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        require(!(cal1 == null || cal2 == null)) { "The dates must not be null" }
        return cal1[Calendar.ERA] == cal2[Calendar.ERA] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR] && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }

    private fun getTotalWeeks(calendar: Calendar?): Int {
        return calendar?.getActualMaximum(Calendar.WEEK_OF_MONTH) ?: 0
    }

    fun getTotalWeeks(date: Date?): Int {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin"))
        cal.time = date
        return getTotalWeeks(cal)
    }

    fun isPastDay(date: Date): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin"))
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return date.before(calendar.time)
    }

    fun isPastTwelve(date: Date): Boolean {
        var date = date
        return if (date.hours >= 12) {
            val c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin"))
            c.time = date
            c.add(Calendar.DATE, 2)
            date = c.time
            date.before(c.time)
        } else {
            val c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin"))
            c.time = date
            c.add(Calendar.DATE, 1)
            date = c.time
            date.before(c.time)
        }
    }
}