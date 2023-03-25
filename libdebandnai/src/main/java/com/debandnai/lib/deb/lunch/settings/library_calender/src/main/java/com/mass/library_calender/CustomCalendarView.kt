package com.mass.library_calender

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.mass.library_calender.Constants.Companion.ORDER_DATE
import com.mass.library_calender.Constants.Companion.SCHOOL_HOLIDAY
import com.mass.library_calender.Constants.Companion.STUDENT_HOLIDAY
import com.mass.library_calender.Constants.Companion.dateFormatHourMinuits
import com.mass.library_calender.Constants.Companion.timeZone
import java.text.DateFormatSymbols
import java.util.*


class CustomCalendarView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null
) :
    LinearLayout(mContext, attrs) {
    private var view: View? = null
    private var previousMonthButton: ImageView? = null
    private var nextMonthButton: ImageView? = null
    private var crossDayOfWeek1: ImageView? = null
    private var crossDayOfWeek2: ImageView? = null
    private var crossDayOfWeek3: ImageView? = null
    private var crossDayOfWeek4: ImageView? = null
    private var crossDayOfWeek5: ImageView? = null
    private var crossDayOfWeek6: ImageView? = null
    private var crossDayOfWeek7: ImageView? = null
    private var calendarListener: CalendarListener? = null
    /*var hour:Int=0
    var minuits:Int=0*/
    var time:String="00:00"

    var currentCalendar: Calendar? = null
        private set
    private var locale: Locale? = null
    private var lastSelectedDay: Date? = null
    var customTypeface: Typeface? = null
    var firstDayOfWeek = Calendar.SUNDAY

    /* private OnClickListener onWeekDayClickListener = new OnClickListener() {
           @Override
           public void onClick(View view) {
               // Extract day selected
             */
    /*  ViewGroup dayOfMonthContainer = (ViewGroup) view;
                  String tagId = (String) dayOfMonthContainer.getTag();
                  tagId = tagId.substring(DAY_OF_WEEK_CONTAINER.length(), tagId.length());


                  if (calendarListener != null)
                      calendarListener.onWeekDaySelectedNew(Integer.parseInt(tagId));*/
    /*



                  ViewGroup dayOfMonthContainer = (ViewGroup) view;
                  String tagId = (String) dayOfMonthContainer.getTag();
                  tagId = tagId.substring(DAY_OF_WEEK_CONTAINER.length(), tagId.length());
                  final TextView dayOfMonthText = (TextView) view.findViewWithTag(DAY_OF_MONTH_TEXT + tagId);

                  // Fire event
                  final Calendar calendar = Calendar.getInstance();
                  calendar.setFirstDayOfWeek(getFirstDayOfWeek());
                  calendar.setTime(currentCalendar.getTime());
                  //calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfMonthText.getText().toString()));
      //            markDayAsSelectedDay(calendar.getTime());

                  //Set the current day color
                  markDayAsCurrentDay(currentCalendar);

                  if (calendarListener != null)
                      calendarListener.onWeekDaySelectedNew(Integer.parseInt(tagId),calendar.getTime(),calendar);

              }
          };*/
    var decorators: List<DayDecorator>? = null
    private var disabledDayBackgroundColor = 0
    private var disabledDayTextColor = 0
    private var calendarBackgroundColor = 0
    private var selectedDayBackground = 0
    private var weekLayoutBackgroundColor = 0
    private var calendarTitleBackgroundColor = 0
    private var selectedDayTextColor = 0
    private var calendarTitleTextColor = 0
    private var dayOfWeekTextColor = 0
    private var dayOfMonthTextColor = 0
    private var currentDayOfMonth = 0
    private var currentMonthIndex = 0
    var isOverflowDateVisible = true
        private set
    private var disabledDayBackgroundDrawable: Drawable? = null
    private var selectedDayBackgroundDrawable: Drawable? = null
    private var studentTeacherMarkedHolidayDateBackDrawable: Drawable? = null
    private var schoolClassKitchenMarkedHolidayDateBackDrawable: Drawable? = null
    private var orderProcessDateBackDrawable: Drawable? = null
    private var pastDaysBackDrawable: Drawable? = null
    private var selectedWeekIndex = 0
    var dateArrayList: MutableList<Date>? = null
    var markedHolidayArrayList: MutableList<Date>? = null
    var markedStudentHoliday: MutableList<Date>? = null
    var orderProcessArrayList: MutableList<Date>? = null
    private var isMondayAvailable = false
    private var isTuesdayAvailable = false
    private var isWednesdayAvailable = false
    private var isThursdayAvailable = false
    private var isFridayAvailable = false




    private fun getAttributes(attrs: AttributeSet?) {
        val typedArray =
            mContext.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView, 0, 0)
        calendarBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_calendarBackgroundColor,
            resources.getColor(R.color.white)
        )
        calendarTitleBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_titleLayoutBackgroundColor,
            resources.getColor(R.color.white)
        )
        calendarTitleTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_calendarTitleTextColor,
            resources.getColor(R.color.white)
        )
        weekLayoutBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_weekLayoutBackgroundColor,
            resources.getColor(R.color.white)
        )
        dayOfWeekTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_dayOfWeekTextColor,
            resources.getColor(R.color.black)
        )
        dayOfMonthTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_dayOfMonthTextColor,
            resources.getColor(R.color.white)
        )
        disabledDayBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_disabledDayBackgroundColor,
            resources.getColor(R.color.day_disabled_background_color)
        )
        disabledDayTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_disabledDayTextColor,
            resources.getColor(R.color.day_disabled_text_color)
        )
        selectedDayBackground = typedArray.getColor(
            R.styleable.CustomCalendarView_selectedDayBackgroundColor,
            resources.getColor(R.color.selected_day_background)
        )
        selectedDayTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_selectedDayTextColor,
            resources.getColor(R.color.white)
        )
        currentDayOfMonth = typedArray.getColor(
            R.styleable.CustomCalendarView_currentDayOfMonthColor,
            resources.getColor(R.color.white)
        )
        selectedDayBackgroundDrawable =
            typedArray.getDrawable(R.styleable.CustomCalendarView_selectedDayBackgroundDrawable)
        disabledDayBackgroundDrawable =
            typedArray.getDrawable(R.styleable.CustomCalendarView_calendarBackgroundDrawable)
        studentTeacherMarkedHolidayDateBackDrawable =
            typedArray.getDrawable(R.styleable.CustomCalendarView_studentTeacherMarkedHolidayDateBackDrawable)
        orderProcessDateBackDrawable =
            typedArray.getDrawable(R.styleable.CustomCalendarView_orderProcessDateBackDrawable)

        schoolClassKitchenMarkedHolidayDateBackDrawable =
            typedArray.getDrawable(R.styleable.CustomCalendarView_schoolClassKitchenMarkedHolidayDateBackDrawable)
        pastDaysBackDrawable =
            typedArray.getDrawable(R.styleable.CustomCalendarView_pastDaysBackDrawable)
        typedArray.recycle()
    }

    private fun initializeCalendar() {
        val inflate = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflate.inflate(R.layout.custom_calendar_layout, this, true)
        previousMonthButton = view?.findViewById<ImageView>(R.id.leftButton)
        nextMonthButton = view?.findViewById<ImageView>(R.id.rightButton)
        crossDayOfWeek1 = view?.findViewById<View>(R.id.crossDayOfWeek1) as ImageView
        crossDayOfWeek2 = view?.findViewById<View>(R.id.crossDayOfWeek2) as ImageView
        crossDayOfWeek3 = view?.findViewById<View>(R.id.crossDayOfWeek3) as ImageView
        crossDayOfWeek4 = view?.findViewById<View>(R.id.crossDayOfWeek4) as ImageView
        crossDayOfWeek5 = view?.findViewById<View>(R.id.crossDayOfWeek5) as ImageView
        crossDayOfWeek6 = view?.findViewById<View>(R.id.crossDayOfWeek6) as ImageView
        crossDayOfWeek7 = view?.findViewById<View>(R.id.crossDayOfWeek7) as ImageView
        previousMonthButton?.setOnClickListener {
            currentMonthIndex--
            currentCalendar = Calendar.getInstance(Locale.getDefault())
            currentCalendar?.add(Calendar.MONTH, currentMonthIndex)
            refreshCalendar(currentCalendar, null, null, null, null)
            if (calendarListener != null) {
                //                    calendarListener.onMonthChanged(currentCalendar.getTime());
                calendarListener?.onMonthChanged(currentCalendar, currentCalendar?.time)
            }
        }
        nextMonthButton?.setOnClickListener {
            currentMonthIndex++
            currentCalendar = Calendar.getInstance(Locale.getDefault())
            currentCalendar?.add(Calendar.MONTH, currentMonthIndex)
            refreshCalendar(currentCalendar, null, null, null, null)
            if (calendarListener != null) {
                //                    calendarListener.onMonthChanged(currentCalendar.getTime());
                calendarListener?.onMonthChanged(currentCalendar, currentCalendar?.getTime())
            }
        }

        // Initialize calendar for current month
        val locale = mContext.resources.configuration.locale
        val currentCalendar = Calendar.getInstance(locale)
        firstDayOfWeek = Calendar.SUNDAY
        refreshCalendar(currentCalendar, null, null, null, null)
    }

    /**
     * Display calendar title with next previous month button
     */
    @SuppressLint("SetTextI18n")
    private fun initializeTitleLayout() {
        val titleLayout = view?.findViewById<View>(R.id.titleLayout)
        titleLayout?.setBackgroundColor(calendarTitleBackgroundColor)
        var dateText =
            DateFormatSymbols(locale).months[currentCalendar?.get(Calendar.MONTH)!!].toString()
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length)
        val dateTitle = view?.findViewById<View>(R.id.dateTitle) as TextView
        dateTitle.setTextColor(calendarTitleTextColor)
        dateTitle.text = dateText + " " + currentCalendar?.get(Calendar.YEAR)
        dateTitle.setTextColor(calendarTitleTextColor)
        if (null != customTypeface) {
            dateTitle.setTypeface(customTypeface, Typeface.BOLD)
        }
    }

    /**
     * Initialize the calendar week layout, considers start day
     */
    @SuppressLint("DefaultLocale")
    private fun initializeWeekLayout() {
        var dayOfWeekContainer: RelativeLayout
        var dayOfWeek: TextView
        var dayOfTheWeekString: String

        //Setting background color white
        val titleLayout = view?.findViewById<View>(R.id.weekLayout)
        titleLayout?.setBackgroundColor(weekLayoutBackgroundColor)
        val weekDaysArray = DateFormatSymbols(locale).shortWeekdays
        for (i in 1 until weekDaysArray.size) {
            dayOfTheWeekString = weekDaysArray[i]
            if (dayOfTheWeekString.length > 3) {
                dayOfTheWeekString = dayOfTheWeekString.substring(0, 3).toUpperCase()
            }
            dayOfWeekContainer = view?.findViewWithTag<View>(
                DAY_OF_WEEK_CONTAINER + getWeekIndex(
                    i,
                    currentCalendar
                )
            ) as RelativeLayout
            dayOfWeek = view?.findViewWithTag<View>(
                DAY_OF_WEEK + getWeekIndex(
                    i,
                    currentCalendar
                )
            ) as TextView
            dayOfWeek.text = dayOfTheWeekString
            dayOfWeek.setTextColor(dayOfWeekTextColor)
            if (null != customTypeface) {
                dayOfWeek.typeface = customTypeface
            }
            dayOfWeekContainer.setOnClickListener(onWeekDayClickListener)
        }
    }

    private fun setDaysInCalendar() {
        val calendar = Calendar.getInstance(locale)
        calendar.time = currentCalendar?.time
        calendar[Calendar.DAY_OF_MONTH] = 1
        calendar.firstDayOfWeek = firstDayOfWeek
        val firstDayOfMonth = calendar[Calendar.DAY_OF_WEEK]

        // Calculate dayOfMonthIndex
        var dayOfMonthIndex = getWeekIndex(firstDayOfMonth, calendar)
        val actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val startCalendar = calendar.clone() as Calendar
        //Add required number of days
        startCalendar.add(Calendar.DATE, -(dayOfMonthIndex - 1))
        val monthEndIndex = 42 - (actualMaximum + dayOfMonthIndex - 1)
        var dayView: DayView
        var dayOfMonthContainer: ViewGroup
        for (i in 1..42) {
            dayOfMonthContainer =
                view?.findViewWithTag<View>(DAY_OF_MONTH_CONTAINER + i) as ViewGroup
            dayView = view?.findViewWithTag<View>(DAY_OF_MONTH_TEXT + i) as DayView
            if (dayView == null) continue

            //Apply the default styles
            dayOfMonthContainer.setOnClickListener(null)
            dayView.bind(startCalendar.time, decorators)
            dayView.visibility = VISIBLE
            if (null != customTypeface) {
                dayView.typeface = customTypeface
            }
            if (CalendarUtils.isSameMonth(calendar, startCalendar)) {
                dayOfMonthContainer.setOnClickListener(onDayOfMonthClickListener)
                //dayView.setBackgroundColor(calendarBackgroundColor);
                /*if(i%7 == selectedWeekIndex)
                {
                    dayView.setBackgroundDrawable(selectedDayBackgroundDrawable);
                }
                else
                {
                    dayView.setBackgroundDrawable(disabledDayBackgroundDrawable);
                }*/dayView.setBackgroundDrawable(disabledDayBackgroundDrawable)
                dayView.setTextColor(dayOfMonthTextColor)
                //Set the current day color
                markDayAsCurrentDay(startCalendar)
            } else {
                dayView.setBackgroundDrawable(disabledDayBackgroundDrawable)
                dayView.setTextColor(disabledDayTextColor)
                if (!isOverflowDateVisible) dayView.visibility =
                    GONE else if (i >= 36 && monthEndIndex.toFloat() / 7.0f >= 1) {
                    dayView.visibility = GONE
                }
            }
            dayView.decorate()
            startCalendar.add(Calendar.DATE, 1)
            dayOfMonthIndex++

            /*Date date = getDaysFromCalendar(startCalendar.getTime());
            Date curr_date = Calendar.getInstance().getTime();

            Log.d("Days",""+i+" "+date);
            Log.d("curr_date",""+i+" "+curr_date);

//            markDayAsDeselectedDay(calendar.getTime());

//            ArrayList<Date> allDateArray = new ArrayList<>();
//            allDateArray.add(date);
//
//            Log.d("allDateArray",""+allDateArray);

            ArrayList<Date> pastDateArray = null;
            if(date.before(curr_date)) {
                pastDateArray = new ArrayList<>();
                pastDateArray.add(date);
            }
            Log.d("pastDateArray",""+pastDateArray);

            if(pastDateArray!=null) {
                for (int j = 0; j < pastDateArray.size(); j++) {
                    Date pastDates = pastDateArray.get(j);
                    Log.d("pastDates",""+j+" "+pastDates);
                    markDayAsPastDays(pastDates);
                }
            }*/
            //val curr_date = Calendar.getInstance().time
            //var currentDateVar = Date()
            val cal = Calendar.getInstance(timeZone)
            if (dateFormatHourMinuits.parse(dateFormatHourMinuits.format(cal.time))
                    ?.after(dateFormatHourMinuits.parse(time)) == true
            ) {

                //c.time = currentDateVar
                cal.add(Calendar.DATE, 1)
                //currentDateVar = c.time
            }
            if (dateArrayList != null) {
                for (j in dateArrayList?.indices!!) {
                    val date_api = dateArrayList?.get(j)
                    Log.d("Days", "$j $date_api")
                    if (date_api?.after(cal.time) == true) {
                        markDayAsSelectedDay(date_api)
                    }
                    if (date_api?.before(cal.time) == true && (dateFormatHourMinuits.parse(
                            dateFormatHourMinuits.format(cal.time)
                        )?.after(dateFormatHourMinuits.parse(time)) == true)
                    ) {

                        markDayAsPastDays(date_api, ORDER_DATE)
                    }
                }
            }

            if (orderProcessArrayList != null) {
                for (j in orderProcessArrayList?.indices!!) {
                    val date_api = orderProcessArrayList?.get(j)
                    Log.d("Days", "$j $date_api")
                    if (date_api?.after(cal.time) == true) {
                        markDayAsOrderProcessingDay(date_api)
                    } else if (date_api?.before(cal.time) == true && (dateFormatHourMinuits.parse(
                            dateFormatHourMinuits.format(cal.time)
                        )?.after(dateFormatHourMinuits.parse(time)) == true)
                    ) {
                        markDayAsPastDays(date_api, ORDER_DATE)
                    }
                }
            }

            if (markedHolidayArrayList != null) {
                for (j in markedHolidayArrayList?.indices!!) {
                    val date_api = markedHolidayArrayList?.get(j)
                    Log.d("KitchenHolidays", "$j $date_api")
                    if (date_api?.after(cal.time) == true) {
                        markDayAsSchoolCLassKitchenHoliday(date_api)
                    }
                    if (date_api?.before(cal.time) == true && (dateFormatHourMinuits.parse(dateFormatHourMinuits.format(cal.time))?.after(dateFormatHourMinuits.parse(time)) == true)) {
                        markDayAsPastDays(date_api, SCHOOL_HOLIDAY)
                    }
                }
            }
            markedStudentHoliday?.let { studentHoliday ->
                for (j in studentHoliday.indices) {

                    markedStudentHoliday?.get(j)?.let {dateApi->
                        if (dateApi.after(cal.time) && this.markedHolidayArrayList?.contains(studentHoliday[j]) == true) {
                            markDayAsSchoolCLassKitchenHoliday(dateApi)
                        }
                        else if (dateApi.after(cal.time)){
                            markDayAsStudentTeacherHoliday(dateApi)
                        }
                        else {
                            markDayAsPastDays(dateApi, STUDENT_HOLIDAY)
                        }
                    }
                }
            }
//            if (curr_date == Date()) {
////                markDayAsPastDays(curr_date);
//            }
//            try {
//                val date = Date()
//                val dateFormat = SimpleDateFormat("HH:mm")
//                dateFormat.format(date)
//                println(dateFormat.format(date))
//                if ((dateFormatHourMinuits.parse(dateFormatHourMinuits.format(date))?.after(dateFormatHourMinuits.parse(time)) == true)) {
//
//                    var dt = Date()
//                    val c = Calendar.getInstance()
//                    c.time = dt
//                    c.add(Calendar.DATE, 1)
//                    dt = c.time
//                    //                    markDayAsPastDays(dt);
//                    println("Current time is greater than 12.00")
//                } else {
//                    println("Current time is less than 12.00")
//                }
//
//                /*Date EndTime = dateFormat.parse("12:00");
//
//                Log.d("CurrentTime", ""+curr_date);
//
//                if (curr_date.after(EndTime)) {
//                    Log.d("Time has ended ", ""+curr_date);
//                }*/
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            /*if(markedHolidayArrayList!=null) {
                for(int j = 0; j<markedHolidayArrayList.size(); j++) {
                    Date date_api = markedHolidayArrayList.get(j);
                    Log.d("PreviousHolidays",""+j+" "+date_api);
                    markDayAspreviousHoliDay(date_api);
                }
            }*/
        }

        // If the last week row has no visible days, hide it or show it in case
        val weekRow = view?.findViewWithTag<ViewGroup>("weekRow6")
        dayView = view?.findViewWithTag("dayOfMonthText36")!!
        if (dayView.visibility != VISIBLE) {
            weekRow?.visibility = GONE
        } else {
            weekRow?.visibility = VISIBLE
        }

        //resetting week index
        setSelectedWeekIndex(0)
    }

    private fun clearDayOfTheMonthStyle(currentDate: Date?) {
        if (currentDate != null) {
            val calendar = todayCalendar
            calendar.firstDayOfWeek = firstDayOfWeek
            calendar.time = currentDate
            val dayView = getDayOfMonthText(calendar)
            //dayView.setBackgroundColor(calendarBackgroundColor);
            dayView.setBackgroundDrawable(disabledDayBackgroundDrawable)
            //dayView.setTextColor(dayOfWeekTextColor);
            dayView.setTextColor(dayOfMonthTextColor)
            dayView.decorate()
        }
    }

    private fun getDayOfMonthText(currentCalendar: Calendar): DayView {
        return getView(DAY_OF_MONTH_TEXT, currentCalendar) as DayView
    }

    private fun getDayIndexByDate(currentCalendar: Calendar): Int {
        val monthOffset = getMonthOffset(currentCalendar)
        val currentDay = currentCalendar[Calendar.DAY_OF_MONTH]
        return currentDay + monthOffset
    }

    private fun getMonthOffset(currentCalendar: Calendar): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = firstDayOfWeek
        calendar.time = currentCalendar.time
        calendar[Calendar.DAY_OF_MONTH] = 1
        val firstDayWeekPosition = calendar.firstDayOfWeek
        Log.d("firstDayWeekPosition", "" + firstDayWeekPosition)
        val dayPosition = calendar[Calendar.DAY_OF_WEEK]
        return if (firstDayWeekPosition == 1) {
            dayPosition - 1
        } else {
            if (dayPosition == 1) {
                6
            } else {
                dayPosition - 2
            }
        }
    }

    private fun getWeekIndex(weekIndex: Int, currentCalendar: Calendar?): Int {
        val firstDayWeekPosition = currentCalendar?.firstDayOfWeek
        return if (firstDayWeekPosition == 1) {
            weekIndex
        } else {
            if (weekIndex == 1) {
                7
            } else {
                weekIndex - 1
            }
        }
    }

    private fun getView(
        key: String,
        currentCalendar: Calendar
    ): View {
        val index = getDayIndexByDate(currentCalendar)
        return view!!.findViewWithTag(key + index)
    }

    private val todayCalendar: Calendar
        get() {
            val currentCalendar = Calendar.getInstance(mContext.resources.configuration.locale)
            currentCalendar.firstDayOfWeek = firstDayOfWeek
            return currentCalendar
        }

    @SuppressLint("DefaultLocale")
    fun refreshCalendar(
        currentCalendar: Calendar?,
        dateArrayList: MutableList<Date>?,
        markedHolidayArrayList: MutableList<Date>?,
        markedStudentHoliday: MutableList<Date>?,
        orderProcessArrayList: MutableList<Date>?,
        orderProcessDate:Date?=null
    ) {
        this.currentCalendar = currentCalendar
        this.dateArrayList = dateArrayList
        this.markedHolidayArrayList = markedHolidayArrayList
        this.markedStudentHoliday = markedStudentHoliday
        this.orderProcessArrayList = orderProcessArrayList
        this.currentCalendar?.firstDayOfWeek = firstDayOfWeek
        locale = mContext.resources.configuration.locale
        if (dateArrayList != null) {
            Log.d("DateArrayListLibrary", dateArrayList.toString())
        }
        if (markedHolidayArrayList != null) {
            Log.d("MarkedHolidayArrListLib", markedHolidayArrayList.toString())
        }
        if (markedStudentHoliday != null) {
            Log.d("MarkedStudentHolArrLib", markedStudentHoliday.toString())
        }
        if (dateArrayList != null) {
            var orderDates: Date? = null
            var dayOfWeekOrderDates = 0
            isMondayAvailable = false
            isTuesdayAvailable = false
            isWednesdayAvailable = false
            isThursdayAvailable = false
            isFridayAvailable = false
            for (i in dateArrayList.indices) {
                orderDates = dateArrayList[i]
                val c = Calendar.getInstance()
                c.time = orderDates // your date is an object of type Date
                dayOfWeekOrderDates = c[Calendar.DAY_OF_WEEK]


                if (orderDates!=orderProcessDate) {
                    when (dayOfWeekOrderDates) {
                        2 -> {
                            isMondayAvailable = true

                        }
                        3 -> {
                            isTuesdayAvailable = true

                        }
                        4 -> {
                            isWednesdayAvailable = true

                        }
                        5 -> {
                            isThursdayAvailable = true

                        }
                        6 -> {
                            isFridayAvailable = true

                        }
                    }
                }

            }
            if (isMondayAvailable) {
                crossDayOfWeek1?.setImageResource(R.drawable.calender_close_inactive)
            } else {
                crossDayOfWeek1?.setImageResource(R.drawable.calender_close_active)
            }
            if (isTuesdayAvailable ) {
                crossDayOfWeek2?.setImageResource(R.drawable.calender_close_inactive)
            } else {
                crossDayOfWeek2?.setImageResource(R.drawable.calender_close_active)
            }
            if (isWednesdayAvailable) {
                crossDayOfWeek3?.setImageResource(R.drawable.calender_close_inactive)
            } else {
                crossDayOfWeek3?.setImageResource(R.drawable.calender_close_active)
            }
            if (isThursdayAvailable) {
                crossDayOfWeek4?.setImageResource(R.drawable.calender_close_inactive)
            } else {
                crossDayOfWeek4?.setImageResource(R.drawable.calender_close_active)
            }
            if (isFridayAvailable) {
                crossDayOfWeek5?.setImageResource(R.drawable.calender_close_inactive)
            } else {
                crossDayOfWeek5?.setImageResource(R.drawable.calender_close_active)
            }
        }

        // Set date title
        initializeTitleLayout()

        // Set weeks days titles
        initializeWeekLayout()

        // Initialize and set days in calendar
        setDaysInCalendar()
    }

    private fun markDayAsCurrentDay(calendar: Calendar?) {
        if (calendar != null && CalendarUtils.isToday(calendar)) {
            Log.e("isPastTwelve: ", calendar.time.toString())
            val dayOfMonth = getDayOfMonthText(calendar)
            dayOfMonth.setTextColor(currentDayOfMonth)
        }
    }

    private fun markDayAsSelectedDay(currentDate: Date) {
        val currentCalendar = todayCalendar
        currentCalendar.firstDayOfWeek = firstDayOfWeek
        currentCalendar.time = currentDate

        // Clear previous marks
        //clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate)

        // Mark current day as selected
        val view = getDayOfMonthText(currentCalendar)
        //view.setBackgroundColor(selectedDayBackground);
        view.background=selectedDayBackgroundDrawable
        view.setTextColor(selectedDayTextColor)
    }
  private fun markDayAsOrderProcessingDay(currentDate: Date) {
        val currentCalendar = todayCalendar
        currentCalendar.firstDayOfWeek = firstDayOfWeek
        currentCalendar.time = currentDate

        // Clear previous marks
        //clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate)

        // Mark current day as selected
        val view = getDayOfMonthText(currentCalendar)
        view.background=orderProcessDateBackDrawable
        view.setTextColor(selectedDayTextColor)
    }

    private fun markDayAsStudentTeacherHoliday(currentDate: Date) {
        val currentCalendar = todayCalendar
        currentCalendar.firstDayOfWeek = firstDayOfWeek
        currentCalendar.time = currentDate

        // Clear previous marks
        //clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate)

        // Mark current day as selected
        val view = getDayOfMonthText(currentCalendar)
        //view.setBackgroundColor(selectedDayBackground);
        view.background=studentTeacherMarkedHolidayDateBackDrawable
        view.setTextColor(selectedDayTextColor)
    }

    private fun markDayAsSchoolCLassKitchenHoliday(currentDate: Date) {
        val currentCalendar = todayCalendar
        currentCalendar.firstDayOfWeek = firstDayOfWeek
        currentCalendar.time = currentDate

        // Clear previous marks
        //clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate)

        // Mark current day as selected
        val view = getDayOfMonthText(currentCalendar)
        //view.setBackgroundColor(selectedDayBackground);
        view.setBackgroundDrawable(schoolClassKitchenMarkedHolidayDateBackDrawable)
        view.setTextColor(selectedDayTextColor)
    }

    var ii = 0
    private fun markDayAsPastDays(currentDate: Date, tag: String) {
        ii++
        Log.d("$ii[$tag] chk_CdateNaiBis : ", currentDate.toString() + "")
        val currentCalendar = todayCalendar
        currentCalendar.firstDayOfWeek = firstDayOfWeek
        currentCalendar.time = currentDate

        // Clear previous marks
        //clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate)

        // Mark current day as selected
        val view = getDayOfMonthText(currentCalendar)
        //view.setBackgroundColor(selectedDayBackground);
        view.setBackgroundDrawable(pastDaysBackDrawable)
        view.setTextColor(selectedDayTextColor)
    }

    fun getDaysFromCalendar(currentDate: Date): Date? {
        val currentCalendar = todayCalendar
        currentCalendar.firstDayOfWeek = firstDayOfWeek
        currentCalendar.time = currentDate

        // Clear previous marks
        //clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValuesAsDates(currentDate)
        return storeLastValuesAsDates(currentDate)
    }

    private fun storeLastValues(currentDate: Date) {
        lastSelectedDay = currentDate
    }

    private fun storeLastValuesAsDates(currentDate: Date): Date? {
        lastSelectedDay = currentDate
        return lastSelectedDay
    }

    fun setCalendarListener(calendarListener: CalendarListener?) {
        this.calendarListener = calendarListener
    }

    private val onDayOfMonthClickListener =
        OnClickListener { view -> // Extract day selected
            val dayOfMonthContainer = view as ViewGroup
            var tagId = dayOfMonthContainer.tag as String
            tagId = tagId.substring(DAY_OF_MONTH_CONTAINER.length, tagId.length)
            val dayOfMonthText = view.findViewWithTag<View>(DAY_OF_MONTH_TEXT + tagId) as TextView

            // Fire event
            val calendar  = Calendar.getInstance(Locale.getDefault())

            TimeZone.setDefault(timeZone)
            calendar.firstDayOfWeek = firstDayOfWeek
            calendar.time = currentCalendar?.time!!
            calendar[Calendar.DAY_OF_MONTH] = Integer.valueOf(dayOfMonthText.text.toString())
            //            markDayAsSelectedDay(calendar.getTime());

            //Set the current day color
//            markDayAsCurrentDay(currentCalendar)
            if (calendarListener != null) calendarListener?.onDateSelected(calendar.time, calendar)
        }
    private val onWeekDayClickListener =
        OnClickListener { view -> // Extract day selected
            val dayOfMonthContainer = view as ViewGroup
            var tagId = dayOfMonthContainer.tag as String
            tagId = tagId.substring(DAY_OF_WEEK_CONTAINER.length, tagId.length)

            val calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = firstDayOfWeek
            calendar.time = currentCalendar?.time
            //  calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfMonthText.getText().toString()));
            //            markDayAsSelectedDay(calendar.getTime());
            if (calendarListener != null) calendarListener?.onWeekDaySelected(
                calendar.time,
                tagId.toInt(),
                calendar
            )
        }

    fun setShowOverflowDate(isOverFlowEnabled: Boolean) {
        isOverflowDateVisible = isOverFlowEnabled
    }

    fun setSelectedWeekIndex(selectedWeekIndex: Int) {
        this.selectedWeekIndex = selectedWeekIndex
    }

    companion object {
        private const val DAY_OF_WEEK = "dayOfWeek"
        private const val DAY_OF_WEEK_CONTAINER = "dayOfWeekContainer"
        private const val DAY_OF_MONTH_TEXT = "dayOfMonthText"
        private const val DAY_OF_MONTH_CONTAINER = "dayOfMonthContainer"
    }

    init {

        getAttributes(attrs)
        initializeCalendar()

//        getFirstDateOfMonth(new Date());
    }
}