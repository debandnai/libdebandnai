package ie.healthylunch.app.ui

import android.icu.util.Calendar
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.CalenderRepository
import ie.healthylunch.app.data.viewModel.CalenderViewModel
import ie.healthylunch.app.databinding.ActivityCalendarBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_DISABLE_TAG
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_DISABLE_TIME
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_END_DATE_TAG
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.dayFormatYearMonthDay
import ie.healthylunch.app.utils.UserPreferences
import ie.healthylunch.app.utils.UserPreferences.Companion.getAsObject

class CalendarActivity : BaseActivity<CalenderViewModel, CalenderRepository>() {
    lateinit var binding: ActivityCalendarBinding
      
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this@CalendarActivity, R.color.menu_bg_color)
        setUpViewModel()
        binding = DataBindingUtil.setContentView(this@CalendarActivity, R.layout.activity_calendar)

        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.calendarActivity = this

        // get holiday list response
        viewModel.getHolidayListResponse()

        // get SaveHolidayResponse list response
        viewModel.getSaveHolidayResponse()

        // get Delete Holiday Response list response
        viewModel.getDeleteHolidayResponse()

        init()
    }
    override fun onResume() {
        super.onResume()
        viewModel.studentListApiCall()
    }


    override fun getViewModel() = CalenderViewModel::class.java
    override fun getRepository() =
        CalenderRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    private fun init() {
        val calendarDisableTime: String? =
            UserPreferences.getAsObject<String>(this@CalendarActivity,CALENDAR_DISABLE_TIME)
            viewModel.calendarDisableTime.value=calendarDisableTime


        getAsObject<String>(this@CalendarActivity,CALENDAR_END_DATE_TAG)?.let {calenderLastDate->
            val calendar = Calendar.getInstance()
            calendar.time=dayFormatYearMonthDay.parse(calenderLastDate )
            calendar.add(Calendar.DATE, STATUS_ONE)
            viewModel.lastDate = calendar.time
            viewModel.lastDateWeekIndex=calendar.get(Calendar.DAY_OF_WEEK)
        }
    }
}