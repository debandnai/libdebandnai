package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mass.library_calender.CalendarListener
import com.mass.library_calender.CustomCalendarView
import com.mass.library_calender.DayDecorator
import com.mass.library_calender.DayView
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.StudentPagerAdapter
import ie.healthylunch.app.data.model.calender_school_holiday_or_student_holiday.CalenderSchoolHolidayOrStudentHoliday
import ie.healthylunch.app.data.model.holidayDeletedModel.holidaydeletedResponse
import ie.healthylunch.app.data.model.holidayResponse.CalendarData
import ie.healthylunch.app.data.model.holidayResponse.CalendarListResponse
import ie.healthylunch.app.data.model.holidaySavedModel.holidaysavedResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.saveHolidayForSessionModel.saveHolidayforsessionResponse
import ie.healthylunch.app.data.model.studentListModel.DataItem
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.CalenderRepository
import ie.healthylunch.app.data.viewModel.DashBoardViewModel.Companion.selectedStudentPrePosition
import ie.healthylunch.app.databinding.CalendarWarningDialogBinding
import ie.healthylunch.app.ui.CalendarActivity
import ie.healthylunch.app.ui.MenuTemplateActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_DAY
import ie.healthylunch.app.utils.Constants.Companion.DAY_NAME
import ie.healthylunch.app.utils.Constants.Companion.DELETE_HOLIDAY
import ie.healthylunch.app.utils.Constants.Companion.DELETE_TAG
import ie.healthylunch.app.utils.Constants.Companion.FRIDAY
import ie.healthylunch.app.utils.Constants.Companion.FROM_TAG
import ie.healthylunch.app.utils.Constants.Companion.HOLIDAY_LIST
import ie.healthylunch.app.utils.Constants.Companion.MONDAY
import ie.healthylunch.app.utils.Constants.Companion.NO_ORDER_TAG
import ie.healthylunch.app.utils.Constants.Companion.ORDER_1_TAG
import ie.healthylunch.app.utils.Constants.Companion.ORDER_2_TAG
import ie.healthylunch.app.utils.Constants.Companion.ORDER_DATE
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SAVE_HOLIDAY
import ie.healthylunch.app.utils.Constants.Companion.SAVE_HOLIDAY_SESSION
import ie.healthylunch.app.utils.Constants.Companion.SAVE_TAG
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_STUDENT_ID
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_STUDENT_POSITION
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FIVE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FOUR
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_SIX
import ie.healthylunch.app.utils.Constants.Companion.STATUS_THREE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_LIST
import ie.healthylunch.app.utils.Constants.Companion.TEACHER
import ie.healthylunch.app.utils.Constants.Companion.THURADAY
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TUESDAY
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE
import ie.healthylunch.app.utils.Constants.Companion.WEDNESDAY
import ie.healthylunch.app.utils.Constants.Companion.dateMonthYearFormat
import ie.healthylunch.app.utils.Constants.Companion.dayFormatYearMonthDay
import ie.healthylunch.app.utils.Constants.Companion.dayFormatyear
import ie.healthylunch.app.utils.Constants.Companion.hourMinuteFormat
import ie.healthylunch.app.utils.Constants.Companion.timeZone
import ie.healthylunch.app.utils.MethodClass.calenderOffDialog
import ie.healthylunch.app.utils.MethodClass.compareNextDate
import ie.healthylunch.app.utils.MethodClass.compareTwoDate
import ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CalenderViewModel(private val repository: CalenderRepository) : ViewModel(),
    DialogOkListener {
    private var handlerWaitTime: MutableLiveData<Long> = MutableLiveData(1000)
    private var selectedDate: String? = null
    var calendarWarningMessage: MutableLiveData<String> = MutableLiveData("")
    var calendarWarningDescription: MutableLiveData<String> = MutableLiveData("")
    var orderType: MutableLiveData<String> = MutableLiveData("")
    var hasOrder: MutableLiveData<Boolean> = MutableLiveData()
    var warningWeekIndex: MutableLiveData<Int> = MutableLiveData()
    var orderDate: MutableLiveData<Date> = MutableLiveData()

    @SuppressLint("StaticFieldLeak")
    var viewPager2: ViewPager2? = null
    private var lastClickTime1: Long = 0
    private var lastClickTime2: Long = 0
    private var lastClickTime3: Long = 0
    private var lastClickTime4: Long = 0
    var weekIndexForAppController = STATUS_ZERO
    var saveDelete = ""
    lateinit var dateForAppController: Date
    private var userCalendar: Calendar? = null
    var dateArrayList = mutableListOf(Date())
    var orderProcessArrayList = mutableListOf(Date())
    val calender_day_count = 30
    var markedHolidayArrayList = mutableListOf(Date())
    var calendarDisableTime: MutableLiveData<String> = MutableLiveData("")

    init {
        Constants.DASHBOARD_ID = STATUS_ZERO
    }

    //Holiday Type-school holiday or student holiday
    private var markedSchoolHolidayArrayList: MutableList<CalenderSchoolHolidayOrStudentHoliday> =
        ArrayList()
    var markedStudentHoliday = mutableListOf(Date())
    var month: Int = STATUS_ZERO
    var year: Int = STATUS_ZERO

    var isToday: Boolean = false
    var isNextDayNoon: Boolean = false
    var formattedDateApiString = ""
    var isViewOnly: Boolean = false

    lateinit var childName: String
    lateinit var currentPageNew: String
    var msg: MutableLiveData<String> = MutableLiveData("")

    @SuppressLint("StaticFieldLeak")
    lateinit var calendarActivity: CalendarActivity

    @SuppressLint("StaticFieldLeak")
    private var calendarView: CustomCalendarView? = null
    private var currentCalendar: Calendar? = null
    private var firstDayOfMonth: Date? = Date()
    private var lastDayOfMonth: Date? = Date()
    private var currentDateVar: Date? = Date()
    private var mondayArrayList: ArrayList<Date?>? = null
    private var tuesdayArrayList: ArrayList<Date?>? = null
    private var wednesdayArrayList: ArrayList<Date?>? = null
    private var thursdayArrayList: ArrayList<Date?>? = null
    private var fridayArrayList: ArrayList<Date?>? = null
    var studentList: List<DataItem> = ArrayList()
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val simpleDateDayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private var isMondayAvailable = false
    private var isTuesdayAvailable = false
    private var isWednesdayAvailable = false
    private var isThursdayAvailable = false
    private var isFridayAvailable = false
    private var isOrderHolAv = false

    private var isMondayMenuAvailable = false
    private var isTuesdayMenuAvailable = false
    private var isWednesdayMenuAvailable = false
    private var isThursdayMenuAvailable = false
    private var isFridayMenuAvailable = false

    //private var firstDate:Date? = null
    var lastDate: Date? = null
    var lastDateWeekIndex = 0
    private var orderProcessDate: Date? = null

    //Student List
    private val _studentModel: SingleLiveEvent<Resource<StudenListResponse>?> =
        SingleLiveEvent()
    private val studentModelResponse: SingleLiveEvent<Resource<StudenListResponse>?>
        get() = _studentModel

    fun studentList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _studentModel.value = repository.studentList(jsonObject, token)
    }

    // Holiday list
    private val _holyDayListResponse: SingleLiveEvent<Resource<CalendarListResponse>?> =
        SingleLiveEvent()
    private val holyDayListResponse: SingleLiveEvent<Resource<CalendarListResponse>?>
        get() = _holyDayListResponse

    private fun holyDayList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _holyDayListResponse.value = repository.holidayList(jsonObject, token)
    }

    // Delete Holiday list
    private val _deleteHolyDayResponse: SingleLiveEvent<Resource<holidaydeletedResponse>?> =
        SingleLiveEvent()
    private val deleteHolyDayResponse: SingleLiveEvent<Resource<holidaydeletedResponse>?>
        get() = _deleteHolyDayResponse

    private fun deleteHolyDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _deleteHolyDayResponse.value = repository.deleteHoliday(jsonObject, token)
    }

    // Save Holiday list
    private val _saveHolyDayResponse: SingleLiveEvent<Resource<holidaysavedResponse>?> =
        SingleLiveEvent()
    private val saveHolyDayResponse: SingleLiveEvent<Resource<holidaysavedResponse>?>
        get() = _saveHolyDayResponse

    private fun saveHolyDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _saveHolyDayResponse.value = repository.saveHoliday(jsonObject, token)
    }

    // Save Holiday session list
    private val _saveHolyDaySessionResponse: SingleLiveEvent<Resource<saveHolidayforsessionResponse>?> =
        SingleLiveEvent()
    private val saveHolyDaySessionResponse: SingleLiveEvent<Resource<saveHolidayforsessionResponse>?>
        get() = _saveHolyDaySessionResponse

    private fun saveHolyDaySession(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _saveHolyDaySessionResponse.value = repository.saveHolidaySession(jsonObject, token)
    }


    fun setStudentFinitePagerContainer(view: FinitePagerContainer) {
        MethodClass.setUpStudentFinitePagerContainer(view)
    }

    fun studentListApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                calendarActivity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(calendarActivity)

        studentList(jsonObject, TOKEN)
    }


    fun setViewPagerFromAppController() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            viewPager2?.let { setViewPager(it, calendarActivity) }
        }
    }

    //For student List
    fun setViewPager(view: ViewPager2, activity: Activity) {
        viewPager2 = view
        calendarActivity = activity as CalendarActivity
        MethodClass.showProgressDialog(calendarActivity)
        view.offscreenPageLimit = 4

        studentModelResponse.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                    if (it.value.response.raws.data.isNotEmpty()) {
                        UserPreferences.saveAsObject(
                            activity,
                            it.value.response.raws.data,
                            "TEMP_CALENDER_DATA"
                        )
                        studentList = it.value.response.raws.data

                        val studentPagerAdapter =
                            StudentPagerAdapter(activity, it.value.response.raws.data)
                        view.adapter = studentPagerAdapter
                        view.isFocusable = false
                        view.currentItem = SELECTED_STUDENT_POSITION


                        _studentModel.value = null
                        studentModelResponse.value = _studentModel.value

                    } else {
                        studentList = ArrayList()
                        AppController.getInstance().isDeActivatedAllStudents = true
                        AppController.getInstance().logoutAppController(activity, true)
                    }
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                activity,
                                null,
                                STUDENT_LIST,
                                REFRESH_TOKEN
                            )
                    else if (it.errorCode == 307 || it.errorCode == 426)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                activity,
                                it1, it.errorCode
                            )
                        }

                }
                else -> {}
            }

        }

        view.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MethodClass.showProgressDialog(calendarActivity)
                SELECTED_STUDENT_POSITION = position
                selectedStudentPrePosition =
                    position// this is because in dashboard, selectedStudentPrePosition will be same as SELECTED_STUDENT_POSITION

                SELECTED_STUDENT_ID = studentList[position].studentid
                USER_TYPE = studentList[position].user_type
                childName =
                    studentList[position].fName + " " + studentList[position].lName
                currentPageNew = position.toString()
                //When Student change holiday list API will call
                /*lastDate = null
                getLastDate()*/
                returnDatesDayWise()
                holidayListApi()

            }

        })
    }

    //For calender init
    fun setCalender(view: CustomCalendarView) {
        calendarView = view

        currentCalendar = Calendar.getInstance(Locale.getDefault())
        calendarView?.firstDayOfWeek = Calendar.MONDAY
        calendarView?.setShowOverflowDate(false)
        allWeekListPrimaryState()
        getFirstDayOfMonth(firstDayOfMonth)
        getLastDayOfMonth(lastDayOfMonth)
        returnDatesDayWise()
        (currentCalendar?.get(Calendar.MONTH))?.let { mnth ->
            month = mnth + STATUS_ONE

        }
        (currentCalendar?.get(Calendar.YEAR))?.let { yr ->
            year = yr
        }

        //Initialize calendar with date
        userCalendar = Calendar.getInstance(Locale.getDefault())
        TimeZone.setDefault(timeZone)
        userCalendar?.time = Date()
        month = userCalendar?.get(Calendar.MONTH)?.plus(1)!!
        year = userCalendar?.get(Calendar.YEAR)!!
        userCalendar?.let { cal ->
            calendarDisableTime.value?.let { time -> calendarView!!.time = time }
            if (userCalendar?.time?.after(setTimeFormat(cal)) == true
            ) {
                userCalendar?.time = currentDateVar
                userCalendar?.add(Calendar.DATE, STATUS_TWO)
            } else {
                userCalendar?.time = currentDateVar
                userCalendar?.add(Calendar.DATE, STATUS_ONE)
            }
            currentDateVar = userCalendar?.time
        }
        calendarView?.setCalendarListener(object : CalendarListener {

              
            @SuppressLint("SimpleDateFormat")
            override fun onDateSelected(date: Date?, calendar: Calendar?) {
                calendarView?.context?.let { ctx ->
                    var isDate = false
                    var isOrder = false
                    var isSchoolHoliday = 1
                    val alertMessage: String
                    if (date != null && !isViewOnly
                    ) {
                        currentCalendar = calendar
                        //isToday = compare Two date(Date(), date)
                        isToday = Date() == date
                        /* if (lastDate?.before(date)==true)
                        return*/
                        selectedDate = dayFormatYearMonthDay.format(date)
                        hourMinuteFormat.format(date)
                        isNextDayNoon = if (hourMinuteFormat.parse(hourMinuteFormat.format(date))
                                ?.after(hourMinuteFormat.parse(calendarDisableTime.value)) == true
                        ) {
                            compareNextDate(Date(), currentDateVar, date)

                        } else {
                            false

                        }
                        Log.e("Date()", Date().toString())
                        Log.e("Date()2", currentDateVar.toString())
                        Log.e("Date()3", date.toString())
                        val isMarked: Boolean
                        var isProcessOrder = false
                        orderProcessDate.let {
                            isProcessOrder = setDateFormat(date) == setDateFormat(orderProcessDate)
                        }


                        if (date.after(currentDateVar) && !isToday && !isNextDayNoon) {
                            formattedDateApiString = simpleDateFormat.format(date)

                            for (dateValue in dateArrayList) {
                                if (compareTwoDate(dateValue, date)) {
                                    isDate = true
                                    isOrder = true
                                    break
                                } else isDate = false
                            }
                            for (dateValue in markedStudentHoliday) {
                                if (compareTwoDate(dateValue, date)) {
                                    isOrder = false
                                    break
                                }
                            }
                            for (dateValue in markedHolidayArrayList) {
                                if (compareTwoDate(dateValue, date)) {
                                    isOrder = false
                                    break
                                }
                            }
                            for (schoolHoliday in markedSchoolHolidayArrayList) {

                                if (compareTwoDate(schoolHoliday.dateValueStudentHoliday, date)) {
                                    isSchoolHoliday = schoolHoliday.schoolHolidayStatus
                                    //  isSchoolHoliday = schoolHoliday.schoolHolidayStatus==1
                                    //isSchoolHoliday = schoolHoliday.schoolHolidayStatus == 1
                                    if (schoolHoliday.schoolHolidayStatus!= STATUS_ONE && schoolHoliday.schoolHolidayStatus!= STATUS_FIVE) {
                                        break
                                    }
                                }

                            }

                            CURRENT_DAY = date.day
                            val daysName = simpleDateDayFormat.format(date)
                            isMarked = isDate && isOrder
                            //1=>Student; 2=>Class; 3=>School; 4=>Kitchen; 5=>Teacher;  // Holiday for
                            if (isSchoolHoliday == STATUS_ONE || isSchoolHoliday == STATUS_FIVE) {
                                currentDateVar?.let { currentDate ->
                                    var selectedLastDate: Date? = null
                                    //User can't order after last date
                                    if (lastDate?.before(date) == true) {
                                        val cal = Calendar.getInstance()
                                        cal.time = lastDate
                                        cal.add(Calendar.DATE, STATUS_MINUS_ONE)
//                                        when (cal.get(Calendar.DAY_OF_WEEK)) {
//                                            Calendar.SATURDAY -> {
//                                                cal.add(Calendar.DATE, STATUS_MINUS_ONE)
//                                            }
//                                            Calendar.SUNDAY -> {
//                                                cal.add(Calendar.DATE, STATUS_MINUS_TWO)
//                                            }
//
//                                        }
                                        selectedLastDate = cal.time
                                        cal.time = currentDate
                                        when (cal.get(Calendar.DAY_OF_WEEK)) {
                                            Calendar.SATURDAY -> {
                                                cal.add(Calendar.DATE, STATUS_TWO)
                                            }
                                            Calendar.SUNDAY -> {
                                                cal.add(Calendar.DATE, STATUS_ONE)
                                            }
                                        }
                                        val startDate = cal.time
                                        val strDt = dateMonthYearFormat.format(startDate)
                                        val endDt = selectedLastDate?.let { dateMonthYearFormat.format(it) }
                                        calendarView?.context?.let { view ->
                                            val status = if (isMarked || isDate) {
                                                view.getString(R.string.remove_holiday2)
                                            } else {
                                                view.getString(R.string.update_order)
                                            }
                                            val msg =view.getString(R.string.you_can_add_or_remove_holiday_for, status, strDt, endDt)
//                                            msg += view.getString(
//                                                R.string.from_calendar,
//                                                simpleDateFormat.format(startDate),
//                                                //simpleDateFormat.format(selectedLastDate)
//                                                lastDate?.let { simpleDateFormat.format(it) }
//                                            )
                                            MethodClass.showToast(view, msg)
                                        }
                                        return
                                    }
                                }




                                when {
                                    isProcessOrder -> {
                                        Toast.makeText(
                                            ctx,
                                            ctx.getString(R.string.this_order_has_already_processed),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                                    isMarked -> {
                                        //For reduce multiple click issue
                                        if (SystemClock.elapsedRealtime() - lastClickTime1 < 2000) {
                                            return
                                        }
                                        calendarWarningMessage.value=ctx.getString(R.string.add_holiday)
                                        calendarWarningDescription.value=""
                                        orderType.value  =ORDER_2_TAG
                                        hasOrder.value  =true
                                        calendarErrorDialog()
                                       
                                        //Set real time
                                        lastClickTime1 = SystemClock.elapsedRealtime()
                                        //saveHolidayApi()
                                    }
                                    isDate -> {
                                        //For reduce multiple click issue
                                        if (SystemClock.elapsedRealtime() - lastClickTime2 < 2000) {
                                            return
                                        }
                                       

                                        calendarWarningMessage.value=ctx.getString(R.string.remove_holiday_for_single_day)
                                        calendarWarningDescription.value=""
                                        orderType.value  =ORDER_2_TAG
                                        hasOrder.value  =false
                                        calendarErrorDialog()
                                        //Set real time
                                        lastClickTime2 = SystemClock.elapsedRealtime()
                                        //
                                        //  deleteHolidayApi()

                                    }
                                    else -> {
                                        //New Order
                                        orderMenuOrNot()
                                        DAY_NAME = daysName

                                    }
                                }
                            } else {

                                // For school holiday
                                /*var schoolHolidayMessage = ""
                            schoolHolidayMessage = when (isSchoolHoliday) {

                                2 -> calendarActivity.getString(R.string.this_is_a_class_holiday)
                                3 -> calendarActivity.getString(R.string.this_is_a_school_holiday)
                                4 -> calendarActivity.getString(R.string.this_is_a_kitchen_holiday)
                                else -> calendarActivity.getString(R.string.this_is_a_school_holiday)
                            }*/
//                            CustomDialog.showOkTypeDialog(
//                                calendarActivity,
//                                calendarActivity.getString(R.string.this_is_a_school_holiday),
//                                this@CalenderViewModel
//                            )


                                calenderOffDialog(ctx)

                            }


                        } else Toast.makeText(
                            ctx,
                            ctx.getString(R.string.passed_day_message),
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }
            }

              
            override fun onMonthChanged(calendar: Calendar?, time: Date?) {

                month = STATUS_ONE + calendar?.get(Calendar.MONTH)!!
                year = calendar.get(Calendar.YEAR)
                currentCalendar = calendar
                getFirstDayOfMonth(calendar.time)
                getLastDayOfMonth(calendar.time)

                returnDatesDayWise()
                holidayListApi()
            }

              
            override fun onWeekDaySelected(
                date: Date?,
                weekIndex: Int,
                calendar: Calendar?
            ) {
                //  calendarView.setSelectedWeekIndex(weekIndex);

                calendarView!!.setSelectedWeekIndex(weekIndex)

                currentCalendar = calendar
                when (weekIndex) {
                    STATUS_ONE -> checkWeek(
                        MONDAY,
                        weekIndex,
                        mondayArrayList!!,
                        isMondayAvailable,
                        isMondayMenuAvailable,
                        date
                    )
                    STATUS_TWO -> checkWeek(
                        TUESDAY,
                        weekIndex,
                        tuesdayArrayList!!,
                        isTuesdayAvailable,
                        isTuesdayMenuAvailable,
                        date
                    )
                    STATUS_THREE -> checkWeek(
                        WEDNESDAY,
                        weekIndex,
                        wednesdayArrayList!!,
                        isWednesdayAvailable,
                        isWednesdayMenuAvailable,
                        date
                    )
                    STATUS_FOUR -> checkWeek(
                        THURADAY,
                        weekIndex,
                        thursdayArrayList!!,
                        isThursdayAvailable,
                        isThursdayMenuAvailable,
                        date
                    )
                    STATUS_FIVE -> checkWeek(
                        FRIDAY,
                        weekIndex,
                        fridayArrayList!!,
                        isFridayAvailable,
                        isFridayMenuAvailable,
                        date
                    )
                }
            }

            override fun onWeekDaySelectedNew(
                weekIndex: Int,
                date: Date?,
                time: Calendar?
            ) {

            }
        })
        val decorators: MutableList<DayDecorator> =
            ArrayList()
        decorators.add(DisabledColorDecorator())
        calendarView?.decorators = decorators
        calendarView?.refreshCalendar(
            currentCalendar,
            null,
            null,
            null,
            null

        )
        // calendarView?.markDayAsSelectedDay(java.util.Date("Wed Aug 11 00:00:00 GMT+05:30 2021"))

    }


      
    private fun orderMenuOrNot() {
        calendarView?.context?.let {ctx->
            calendarWarningMessage.value = ctx.getString(R.string.no_order_message)
            orderType.value = NO_ORDER_TAG
            hasOrder.value = false
            calendarWarningDescription.value = ctx.getString(R.string.would_you_like_to_place_an_order)
            calendarErrorDialog()
        }
    }

      
    @SuppressLint("SimpleDateFormat")
    private fun checkWeek(
        dayName: String,
        weekIndex: Int,
        dayArrayList: ArrayList<Date?>?,
        isDayAvailable: Boolean,
        isDayMenuAvailable: Boolean,
        date: Date?
    ) {
        calendarView?.context?.let { ctx ->
            if (!isViewOnly ) {
                if (dayArrayList.isNullOrEmpty()) {
                    CustomDialog.showOkTypeDialog(calendarActivity,
                        ctx.getString(R.string.no_dates_available),
                        this@CalenderViewModel
                    )
                }else{
                    if (dayArrayList[STATUS_ZERO]?.before(lastDate) == true) {

                        var isStudentHoliday = true

                        // If all week days is school holiday and not student/teacher holiday
                        if(checkIsAllWeekDayIsSchoolHoliday(dayArrayList)  && !hasOrder(dayArrayList)){
                            calenderOffDialog(ctx)
                        }else if (isDayAvailable && hasOrder(dayArrayList)) {
//                        isStudentHoliday = false


                            val orderedDateArrayList: MutableList<Date?> = ArrayList()
                            for (date in dayArrayList) {
                                if (dateArrayList.contains(date))
                                    if (!markedStudentHoliday.contains(date))
                                        date?.let { orderedDateArrayList.add(it) }

                            }


                            // For chacking school or kitchen holiday
                            //markedStudentHoliday for student holiday
                            //markedHolidayArrayList for holiday
                            //dayArrayList all day array list
                            //dateArrayList - particular column date array list

                            for (date in dayArrayList) {
                                if (dateArrayList.contains(date)) {
                                    if (!(markedStudentHoliday.contains(date) && !checkIsAllWeekDayIsSchoolHoliday(dayArrayList))) {
                                        isStudentHoliday = false
                                        break
                                    } /*else {
                                    //If student holiday
                                    isStudentHoliday = true
                                }*/
                                }

                            }
                            if (orderedDateArrayList.size == STATUS_ONE && orderedDateArrayList.contains(
                                    orderProcessDate)) {
                                orderedDateArrayList.clear()
                            }
                            //if (particularDayHolidayList.size < noDuplicateDayList.size) {
                            if (orderedDateArrayList.isNotEmpty() && !isStudentHoliday) {
                                if (SystemClock.elapsedRealtime() - lastClickTime3 < 2000) {
                                    return
                                }


                                //Session order for holiday
                                date?.let { date->
                                    calendarWarningMessage.value=ctx.getString(R.string.remove_session_holiday,dayName)
                                    calendarWarningDescription.value  =ctx.getString(R.string.these_lunches_will_not_be_processed)
                                    orderType.value  =ORDER_1_TAG
                                    hasOrder.value  =true
                                    warningWeekIndex.value  =weekIndex
                                    orderDate.value  =date
                                    calendarErrorDialog()
                                }

                                //Set real time
                                lastClickTime3 = SystemClock.elapsedRealtime()
                            } else {
                                //For reduce multiple click issue
                                if (SystemClock.elapsedRealtime() - lastClickTime4 < 2000) {
                                    return
                                }

                                //Session order for remove holiday
                                date?.let { date->
                                    calendarWarningMessage.value=ctx.getString(R.string.remove_holiday,dayName)
                                    calendarWarningDescription.value=""
                                    orderType.value  =ORDER_1_TAG
                                    hasOrder.value  =false
                                    warningWeekIndex.value  =weekIndex
                                    orderDate.value  =date
                                    calendarErrorDialog()

                                }


                                //Set real time
                                lastClickTime4 = SystemClock.elapsedRealtime()
                            }

                        } else {

                            CURRENT_DAY = weekIndex
                            //session button click and goto MenuTemplate
                            //menuTemplate()
                            var intex = STATUS_ZERO
                            if (dayArrayList.contains(orderProcessDate)) {
                                intex = STATUS_ONE
                            } else {
                                intex = STATUS_ZERO
                            }

                            selectedDate = dayArrayList[intex]?.let { selectedDate ->
                                dayFormatYearMonthDay.format(
                                    selectedDate
                                )
                            }
                            DAY_NAME = MethodClass.getDayNameOfIntegerDayValue(weekIndex)
                                .replaceFirstChar(Char::titlecase)
                            orderMenuOrNot()


                        }
                    }
                }
            }
        }
    }

    private fun checkIsAllWeekDayIsSchoolHoliday(dayArrayList: ArrayList<Date?>): Boolean {
        var isAllDaySchoolHoliday=true
        for (date in dayArrayList) {
            if (!(markedHolidayArrayList.contains(date) && !markedStudentHoliday.contains(date))) {
                isAllDaySchoolHoliday =false
                break
            }
        }
        return isAllDaySchoolHoliday
    }

    private fun hasOrder(dayArrayList: ArrayList<Date?>): Boolean {
        var hasdate = false
        for (date in dayArrayList) {
            if (dateArrayList.contains(date)) {
                hasdate = true
            }
        }
        return hasdate
    }




    private fun popupCalendarForWeek(
        weekIndex: Int,
        hasOrder: Boolean,
        date: Date?
    ) {
        MethodClass.showProgressDialog(calendarActivity)
        saveDelete = if (hasOrder)
            SAVE_TAG
        else
            DELETE_TAG
        weekIndexForAppController = weekIndex
        dateForAppController = date ?: Date()
        allDayAsHoliday(weekIndex, saveDelete, date)
    }


    fun allDayAsHolidayForAppController() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            MethodClass.showProgressDialog(calendarActivity)
            allDayAsHoliday(weekIndexForAppController, saveDelete, dateForAppController)
        }
    }


    fun getFirstDayOfMonth(date: Date?): Date? {
        val cal: Calendar? = Calendar.getInstance()
        cal?.clear()
        if (date != null) {
            cal?.time = date
        }
        cal?.set(
            Calendar.DAY_OF_MONTH,
            cal.getActualMinimum(Calendar.DAY_OF_MONTH)
        )
        Log.d("FirstDateOfMonth: ", "" + cal?.time)
        firstDayOfMonth = cal?.time
        return cal?.time
    }

    fun getLastDayOfMonth(date: Date?): Date? {
        val cal: Calendar? = Calendar.getInstance()
        cal?.clear()
        if (date != null) {
            cal?.time = date
        }
        cal?.set(
            Calendar.DAY_OF_MONTH,
            cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        Log.d("LastDateOfMonth: ", "" + cal?.time)
        lastDayOfMonth = cal?.time
        return cal?.time
    }

    fun returnDatesDayWise() {

        val df1: DateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var firstDate: String? = null
        var lastDate: String? = null
        try {
            firstDate = firstDayOfMonth?.let { df1.format(it) }
            lastDate = lastDayOfMonth?.let { df1.format(it) }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val dates: MutableList<Date?> = getDates(firstDate, lastDate)

        allWeekListPrimaryState()
        for (date: Date? in dates) {
            Log.d("IndividualDates", "" + date)
            if (date != null) {
                val cal = Calendar.getInstance()
                cal.time = currentDateVar
                cal.add(Calendar.DATE, -1)
                val beforeCurrentDateVar = cal.time
                Log.d("currentdatejjj", "" + beforeCurrentDateVar)

                if (date.after(beforeCurrentDateVar)) {
                    val c: Calendar? = Calendar.getInstance()
                    if (c != null) {
                        c.time = date
                        // your date is an object of type Date
                        val dayOfWeek: Int = c.get(Calendar.DAY_OF_WEEK)
                        Log.d("DayOfWeek", "" + dayOfWeek)
                        when (dayOfWeek) {
                            2 -> mondayArrayList?.add(date)
                            3 -> tuesdayArrayList?.add(date)
                            4 -> wednesdayArrayList?.add(date)
                            5 -> thursdayArrayList?.add(date)
                            6 -> fridayArrayList?.add(date)
                        }

                    }
                }
            }
        }

    }

    private inner class DisabledColorDecorator : DayDecorator {
        override fun decorate(cell: DayView?) {
            if (cell != null) {
                if (cell.date.before(currentDateVar)) {
//                    val color: Int = android.graphics.Color.parseColor("#C8C7CC")
//                    cell.setBackgroundColor(color)
                    cell.setBackgroundResource(R.drawable.past_days_background)
                    calendarView?.context?.let { ctx ->
                        cell.setTextColor(ContextCompat.getColor(ctx, R.color.black_6))}
                }
            }
        }
    }

    private fun getDates(
        dateString1: String?,
        dateString2: String?
    ): MutableList<Date?> {
        val dates: ArrayList<Date?> = ArrayList<Date?>()
        val df1: DateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = dateString1?.let { df1.parse(it) }
            date2 = dateString2?.let { df1.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1: Calendar? = Calendar.getInstance()
        if (date1 != null) {
            cal1?.time = date1
        }
        val cal2: Calendar? = Calendar.getInstance()
        if (date2 != null) {
            cal2?.time = date2
        }
        if (cal1 != null) {
            while (!cal1.after(cal2)) {
                dates.add(cal1.time)
                cal1.add(Calendar.DATE, 1)
            }
        }
        return dates
    }


    fun holidayListApi() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                calendarActivity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        //calendarActivity
        MethodClass.hideProgressDialog(calendarActivity)
        MethodClass.showProgressDialog(calendarActivity)
        val jsonObject = MethodClass.getCommonJsonObject(calendarActivity)
        jsonObject.addProperty("holidayfor", "1")
        jsonObject.addProperty("month", month.toString())
        jsonObject.addProperty("year", year.toString())
        jsonObject.addProperty("refuserid", SELECTED_STUDENT_ID.toString())
        jsonObject.addProperty("user_type", USER_TYPE)



        holyDayList(jsonObject, TOKEN)
    }


    fun getHolidayListResponse() {
        holyDayListResponse.observe(calendarActivity) {

            when (it) {
                is Resource.Success -> {
                    it.value.response?.raws?.data?.let { data ->
                        isViewOnly = data.isViewOnly == STATUS_ONE
                        data.calendarDisableTime?.let { disAbleTime ->
                            calendarDisableTime.value = disAbleTime
                        }

                        setCalenderAfterHolyDayApi(data.calendar)
                    }

                    _holyDayListResponse.value = null
                    holyDayListResponse.value = _holyDayListResponse.value

                }
                is Resource.Failure -> {
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                calendarActivity,
                                null,
                                HOLIDAY_LIST,
                                REFRESH_TOKEN
                            )
                    else if (it.errorCode == 307 || it.errorCode == 426)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                calendarActivity,
                                it1, it.errorCode
                            )
                        }
                    else
                        nullCalenderSetup()
                    _holyDayListResponse.value = null
                    holyDayListResponse.value = _holyDayListResponse.value


                }

                else -> {}
            }

        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun setCalenderAfterHolyDayApi(holiday_value: ArrayList<ArrayList<CalendarData>?>?) {
        orderProcessDate = null
        isMondayAvailable = false
        isTuesdayAvailable = false
        isWednesdayAvailable = false
        isThursdayAvailable = false
        isFridayAvailable = false

        isOrderHolAv = false
        isMondayMenuAvailable = false
        isTuesdayMenuAvailable = false
        isWednesdayMenuAvailable = false
        isThursdayMenuAvailable = false
        isFridayMenuAvailable = false
        var dayOfWeekOrderDates: Int
        this.dateArrayList.clear()
        this.orderProcessArrayList.clear()
        this.markedHolidayArrayList.clear()
        //Holiday Type-school holiday or student holiday
        this.markedSchoolHolidayArrayList.clear()
        this.markedStudentHoliday.clear()
        if (holiday_value?.isNotEmpty() == true) {
            for (item in holiday_value) {
                if (item != null) {
                    for (items in item) {

                        var orderdate: Date? = null
                        orderdate = items.date?.let { simpleDateFormat.parse(it) }
                        val cal = Calendar.getInstance()
                        cal.time = currentDateVar
                        cal.add(Calendar.DATE, -1)

                        val beforeCurrentDateVar = cal.time

                        if (orderdate?.after(beforeCurrentDateVar) != true)
                            continue

                        //  val calCheckNextMonth = Calendar.getInstance()
                        cal.time = orderdate
                        orderdate = cal.time
                        if (items.order_for != null) {

                            /*val isToday = MethodClass.isToday(items.date)
                                            if (!isToday) {
                                                if (MethodClass.isNextDay(items.date)) {
                                                    if (!MethodClass.isCurrentDayTwelveNoon())
                                                        orderdate?.let { it1 ->
                                                            dateArrayList.add(it1)
                                                        }
                                                } else
                                                    orderdate?.let { it1 -> dateArrayList.add(it1) }
                                            }*/
                            if (month == cal.get(Calendar.MONTH).plus(STATUS_ONE)) {
                                orderdate?.let { orderdate ->
                                    dateArrayList.add(orderdate)
                                    items.order_for?.let { orderFor ->
                                        if (orderFor[STATUS_ZERO] == STATUS_TWO && items.holiday_for.isNullOrEmpty()) {
                                            orderProcessArrayList.add(orderdate)
                                        }

                                        if (items.order_status == STATUS_THREE) {
                                            orderProcessDate = orderdate
                                        }

                                    }
                                }
                            }
                            Log.e("dateArrayList", dateArrayList.toString())

                        } else {
                            //orderdate?.let { it1 -> markedHolidayArrayList.add(it1) }
                            //orderdate?.let { it1 -> markedStudentHoliday.add(it1) }
                            //Holiday Type-school holiday or student holiday
                            /* if (items.holiday_for == null)
                                    orderdate?.let { orderDate ->
                                       *//* if (month == calCheckNextMonth.get(Calendar.MONTH)
                                                .plus(STATUS_ONE)
                                        )*//*
                                            markedSchoolHolidayArrayList.add(
                                                CalenderSchoolHolidayOrStudentHoliday(orderDate, STATUS_ONE)
                                            )
                                    }
                                else {*/
                            items.holiday_for?.let { holidayFor ->
                                orderdate.let { orderDate ->

                                    if (month == cal.get(Calendar.MONTH)
                                            .plus(STATUS_ONE)
                                    )
                                        markedSchoolHolidayArrayList.add(
                                            CalenderSchoolHolidayOrStudentHoliday(
                                                orderDate,
                                                holidayFor[STATUS_ZERO]
                                            )
                                        )
                                }

                                if (Objects.equals(
                                        holidayFor[STATUS_ZERO],
                                        STATUS_ONE
                                    ) or Objects.equals(
                                        holidayFor[STATUS_ZERO],
                                        STATUS_FIVE
                                    )
                                ) {
                                    if (month == cal.get(Calendar.MONTH)
                                            .plus(STATUS_ONE)
                                    )
                                        orderdate?.let { orderDate ->
                                            markedStudentHoliday.add(
                                                orderDate
                                            )
                                        }
                                } else {
                                    if (month == cal.get(Calendar.MONTH)
                                            .plus(STATUS_ONE)
                                    )
                                        orderdate.let { orderDate ->
                                            markedHolidayArrayList.add(
                                                orderDate
                                            )
                                        }
                                }
                            }
                            //  }

                        }

                        val c = Calendar.getInstance()
                        c.time = orderdate
                        dayOfWeekOrderDates = c[Calendar.DAY_OF_WEEK]

                        if (c.time.before(Calendar.getInstance().time)) {
                            continue
                        }

                        /*when (dayOfWeekOrderDates) {
                                        2 -> isMondayAvailable = true
                                        3 -> isTuesdayAvailable = true
                                        4 -> isWednesdayAvailable = true
                                        5 -> isThursdayAvailable = true
                                        6 -> isFridayAvailable = true
                                    }*/
                        //Only selected month session will set.
                        if (month == cal.get(Calendar.MONTH)
                                .plus(STATUS_ONE)
                        ) {
                            if (orderdate != orderProcessDate) {
                                when (dayOfWeekOrderDates) {
                                    STATUS_TWO -> {
                                        isMondayAvailable = true

                                    }
                                    STATUS_THREE -> {
                                        isTuesdayAvailable = true

                                    }
                                    STATUS_FOUR -> {
                                        isWednesdayAvailable = true

                                    }
                                    STATUS_FIVE -> {
                                        isThursdayAvailable = true

                                    }
                                    STATUS_SIX -> {
                                        isFridayAvailable = true

                                    }
                                }
                            }
                        }

                    }
                }
            }
            calendarView?.firstDayOfWeek = Calendar.MONDAY

            calendarView!!.setShowOverflowDate(false)
            if (!dateArrayList.isNullOrEmpty()) {
                dateArrayList.sort()
                val today = Calendar.getInstance()
                today.time = Date()
                // if(month==today.get(Calendar.MONTH).plus(STATUS_ONE)) {
                //    firstDate = dateArrayList[STATUS_ZERO]
                //getLastDate()
                // }
            }
            calendarView!!.refreshCalendar(
                currentCalendar,
                dateArrayList,
                markedHolidayArrayList,
                markedStudentHoliday,
                orderProcessArrayList,
                orderProcessDate
            )


        } else {
            nullCalenderSetup()
        }
        MethodClass.hideProgressDialog(calendarActivity)
        /* handlerWaitTime.value?.let {
             Handler(Looper.getMainLooper()).postDelayed({
                 MethodClass.hideProgressDialog(calendarActivity)
             }, it)
         }*/

        if (msg.value != "") {
            msg.value?.let { message ->
                /*CustomDialog.showInfoTypeDialogwithmsg(
                    calendarActivity,
                    it1
                )*/
                Toast.makeText(calendarActivity, msg.value, Toast.LENGTH_LONG).show()
                // calendarView?.let { calendarView ->showSnackBar(calendarView,message)}
            }

            msg.value = ""
        }

    }

    private fun checkCurrentMonth(orderDate: Date): Boolean {
        /*val currentCalendar = Calendar.getInstance()
        val todayCalendar = Calendar.getInstance()
        currentCalendar.time = orderDate
        val today = Date()
        todayCalendar.time=today*/

        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = orderDate
        return true
    }


    fun deleteHolidayApi() {
        MethodClass.showProgressDialog(calendarActivity)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                calendarActivity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(calendarActivity)
        val deleteDate = JsonArray()
        deleteDate.add(formattedDateApiString)
        jsonObject.addProperty("holidayfor", "1")
        jsonObject.add("holidayon", deleteDate)
        jsonObject.addProperty("user_type", USER_TYPE)
        jsonObject.addProperty("refuserid", SELECTED_STUDENT_ID.toString())
        deleteHolyDay(jsonObject, TOKEN)
    }


    fun getDeleteHolidayResponse() {
        deleteHolyDayResponse.observe(calendarActivity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                    msg.value = it.value.response.raws.success_message
                    handlerWaitTime.value = 2000
                    returnDatesDayWise()
                    holidayListApi()
                    _deleteHolyDayResponse.value = null
                    deleteHolyDayResponse.value = _deleteHolyDayResponse.value

                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                calendarActivity,
                                null,
                                DELETE_HOLIDAY,
                                REFRESH_TOKEN
                            )
                    else {
                        it.errorString.let { it1 ->
                            MethodClass.hideProgressDialog(calendarActivity)
                            it1?.let { it2 ->
                                MethodClass.showErrorDialog(
                                    calendarActivity,
                                    it2,
                                    it.errorCode
                                )
                            }

                        }

                    }
                    _deleteHolyDayResponse.value = null
                    deleteHolyDayResponse.value = _deleteHolyDayResponse.value
                }
                else -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                }
            }
        }

    }


    fun saveHolidayApi() {
        MethodClass.showProgressDialog(calendarActivity)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                calendarActivity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(calendarActivity)
        val deleteDate = JsonArray()
        deleteDate.add(formattedDateApiString)
        jsonObject.addProperty("holidayfor", "1")
        jsonObject.add("holidayon", deleteDate)
        jsonObject.addProperty("user_type", USER_TYPE)
        jsonObject.addProperty("refuserid", SELECTED_STUDENT_ID.toString())
        saveHolyDay(jsonObject, TOKEN)
    }


    fun getSaveHolidayResponse() {
        saveHolyDayResponse.observe(calendarActivity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                    msg.value = it.value.response.raws.success_message
                    handlerWaitTime.value = 2000
                    returnDatesDayWise()
                    holidayListApi()
                    _saveHolyDayResponse.value = null
                    saveHolyDayResponse.value = _saveHolyDayResponse.value
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                calendarActivity,
                                null,
                                SAVE_HOLIDAY,
                                REFRESH_TOKEN
                            )
                    else {
                        it.errorString.let { it1 ->
                            MethodClass.hideProgressDialog(calendarActivity)
                            it1?.let { it2 ->
                                MethodClass.showErrorDialog(
                                    calendarActivity,
                                    it2,
                                    it.errorCode
                                )
                            }

                        }

                    }
                    handlerWaitTime.value = 2000
                    _saveHolyDayResponse.value = null
                    saveHolyDayResponse.value = _saveHolyDayResponse.value
                }
                else -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                }
            }
        }

    }


    // New Order


    fun menuTemplate() {
        selectedDate?.let { ORDER_DATE = it }
        val intent = Intent(calendarActivity, MenuTemplateActivity::class.java)
        intent.putExtra(FROM_TAG, CALENDAR)
        calendarActivity.startActivity(intent)

    }






    private fun allDayAsHoliday(weekIndex: Int, s: String, date: Date?) {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                calendarActivity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        var currentMonth: Int? = date?.month
        currentMonth = currentMonth?.plus(1)

        val currentYear = dayFormatyear.format(date)

        val jsonObject = MethodClass.getCommonJsonObject(calendarActivity)
        jsonObject.addProperty("actiontype", s)
        var holidayFor = STATUS_ONE

        if (USER_TYPE.equals(TEACHER, ignoreCase = true))
            holidayFor = STATUS_FIVE
        jsonObject.addProperty("holidayfor", holidayFor)
        jsonObject.addProperty("refuserid", SELECTED_STUDENT_ID.toString())
        jsonObject.addProperty("weekday", weekIndex.toString())
        jsonObject.addProperty("holiday_month_year", "" + currentMonth + "-" + currentYear)

        saveHolyDaySession(jsonObject, TOKEN)
        msg.value = ""
        saveHolyDaySessionResponse.observe(calendarActivity) {

            when (it) {
                is Resource.Success -> {
                    handlerWaitTime.value = 4000
                    msg.value = ""
                    msg.value = it.value.response.raws.success_message


                    /*when (s) {
                        "save" -> msg.value =
                            calendarActivity.getString(R.string.holiday_save_successfully)
                        else -> msg.value =
                            calendarActivity.getString(R.string.holiday_delete_successful)
                    }*/
                    returnDatesDayWise()
                    holidayListApi()

                    /*Toast.makeText(
                        calendarActivity,
                        it.value.response.raws.success_message,
                        Toast.LENGTH_LONG
                    ).show()*/
                    //  navigate()
                    _saveHolyDaySessionResponse.value = null
                    saveHolyDaySessionResponse.value = _saveHolyDaySessionResponse.value


                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(calendarActivity)
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                calendarActivity,
                                null,
                                SAVE_HOLIDAY_SESSION,
                                REFRESH_TOKEN
                            )
                    else if (it.errorCode == 307 || it.errorCode == 426)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                calendarActivity,
                                it1, it.errorCode
                            )
                        }
                    else {
                        handlerWaitTime.value = 4000

                        _saveHolyDaySessionResponse.value = null
                        saveHolyDaySessionResponse.value = _saveHolyDaySessionResponse.value

                    }
                }
                else -> {}
            }

        }
        //MethodClass.showProgressDialog(calendarActivity)


    }






    private fun nullCalenderSetup() {
        MethodClass.hideProgressDialog(calendarActivity)
        isMondayAvailable = false
        isTuesdayAvailable = false
        isWednesdayAvailable = false
        isThursdayAvailable = false
        isFridayAvailable = false

        isOrderHolAv = false
        isMondayMenuAvailable = false
        isTuesdayMenuAvailable = false
        isWednesdayMenuAvailable = false
        isThursdayMenuAvailable = false
        isFridayMenuAvailable = false
        this.dateArrayList = ArrayList()
        this.markedHolidayArrayList = ArrayList()
        this.markedSchoolHolidayArrayList = ArrayList()
        this.markedStudentHoliday = ArrayList()

        val decorators: MutableList<DayDecorator> =
            ArrayList<DayDecorator>()
        decorators.add(DisabledColorDecorator())
        calendarView?.decorators = decorators
        calendarView?.refreshCalendar(
            currentCalendar,
            dateArrayList,
            markedHolidayArrayList,
            markedStudentHoliday,
            orderProcessArrayList
        )
    }


    override fun okOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }

    private fun allWeekListPrimaryState() {
        mondayArrayList = ArrayList()
        tuesdayArrayList = ArrayList()
        wednesdayArrayList = ArrayList()
        thursdayArrayList = ArrayList()
        fridayArrayList = ArrayList()
    }

    /* private fun getLastDate() {
         currentDateVar?.let {
             val cal1 = Calendar.getInstance()
             cal1.time = currentDateVar
             var count = STATUS_ZERO

             while (count <= calender_day_count) {
                 count++
                 lastDate = cal1.time
                 cal1.add(Calendar.DATE, STATUS_ONE)
             }
         }
     }*/

    fun setDateFormat(date: Date?): Date? {
        val calendar = Calendar.getInstance()
        date?.let {
            calendar.time = date
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }

        return calendar.time
    }

    private fun setTimeFormat(cal: Calendar) :Date{

        val calendarDisableTime = calendarDisableTime.value?.split(":")?.toTypedArray()
        calendarDisableTime?.let { calendarDisable ->
            if (calendarDisable.size > STATUS_ONE) {
                val hour =
                    if (calendarDisable[STATUS_ZERO] != null) calendarDisable[STATUS_ZERO].toInt() else STATUS_ZERO
                val minite =
                    if (calendarDisable[STATUS_ONE] != null) calendarDisable[STATUS_ONE].toInt() else STATUS_ZERO
                cal[Calendar.HOUR_OF_DAY] = hour
                cal[Calendar.MINUTE] = minite
                cal[Calendar.SECOND] = STATUS_ZERO
                cal[Calendar.MILLISECOND] = STATUS_ZERO
            }
        }
        return cal.time
    }

    fun calendarErrorDialog(){
        calendarView?.context?.let { ctx->
            val warningDialog = Dialog(ctx)

            warningDialog.window?.let { window ->
                window.setBackgroundDrawableResource(R.color.transparent)
                window.decorView.setBackgroundResource(android.R.color.transparent)
                window.setDimAmount(0.0f)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val dialogBinding =
                CalendarWarningDialogBinding.inflate(LayoutInflater.from(ctx))

            warningDialog.setContentView(dialogBinding.root)
            warningDialog.setCancelable(false)
            dialogBinding.viewModel=this@CalenderViewModel

            dialogBinding.btnYes.setOnClickListener {
                warningDialog.dismiss()
                if (orderType.value?.equals(ORDER_2_TAG)==true){
                    if (hasOrder.value==true){
                        saveHolidayApi()
                    }
                    else{
                        deleteHolidayApi()
                    }
                }
                else if(orderType.value?.equals(ORDER_1_TAG)==true){
                    if (warningWeekIndex.value!=null && hasOrder.value!=null && orderDate.value!=null) {
                        popupCalendarForWeek(warningWeekIndex.value!!,hasOrder.value!!,orderDate.value)
                    }
                }
                else{
                    menuTemplate()
                }
            }

            dialogBinding.btnNo.setOnClickListener {
                warningDialog.dismiss()
            }
            warningDialog.show()
        }

    }
}
