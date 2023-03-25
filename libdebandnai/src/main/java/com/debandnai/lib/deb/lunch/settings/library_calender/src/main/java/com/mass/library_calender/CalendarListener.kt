package com.mass.library_calender

interface CalendarListener {
    fun onDateSelected(date: java.util.Date?, time: java.util.Calendar?)
    fun onMonthChanged(calendar: java.util.Calendar?, time: java.util.Date?)

    //    void onMonthChanged(Calendar time)
    fun onWeekDaySelected(
        date: java.util.Date?,
        weekIndex: Int,
        time: java.util.Calendar?
    )

    fun onWeekDaySelectedNew(
        weekIndex: Int,
        date: java.util.Date?,
        time: java.util.Calendar?
    )
}