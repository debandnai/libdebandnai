package com.salonsolution.app.data.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.SlotBookingModel
import com.salonsolution.app.data.model.StaffCalendarModel
import com.salonsolution.app.data.model.StaffCalendarTimeSlotModel
import com.salonsolution.app.data.model.TimeSlotModel
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.repository.CartRepository
import com.salonsolution.app.data.repository.StaffRepository
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon
import com.salonsolution.app.utils.UtilsCommon.getZeroTimeDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class StaffCalendarViewModel @Inject constructor(
    private val staffRepository: StaffRepository,
    private val cartRepository: CartRepository
) :
    ViewModel() {


    var noOfDayInAWeek = 6  // how many days show in week list
    var timeSlotInterval = 30 // time slot interval
    var staffId: Int = -1
    var serviceId: Int = -1
    var serviceTime: String = ""
    var staffCalendarResponseLiveData = staffRepository.staffCalendarResponseLiveData
    var weekDaysListLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData(arrayListOf())
    var timeSlotListLiveData: MutableLiveData<ArrayList<StaffCalendarTimeSlotModel>> =
        MutableLiveData(arrayListOf())
    var monthTitleWithYear = MutableLiveData("")
    var currentCalendar: Calendar = Calendar.getInstance()
    var localeCode = Constants.ENGLISH_CODE
    var slotBookingData: ArrayList<SlotBookingModel> = arrayListOf()
    var previousMonthVisible = MutableLiveData(true)
    var previousWeekVisible = MutableLiveData(true)

    private val _calculateTimeSlotsState =
        MutableLiveData<ResponseState<String>>()
    val calculateTimeSlotsState: LiveData<ResponseState<String>>
        get() = _calculateTimeSlotsState
    val addToCartResponseLiveData = cartRepository.staffAddToCartResponseLiveData

    private val _manageSlotsSelectionLiveData =
        MutableLiveData<ResponseState<ArrayList<StaffCalendarTimeSlotModel>>>()
    val manageSlotsSelectionLiveData: LiveData<ResponseState<ArrayList<StaffCalendarTimeSlotModel>>>
        get() = _manageSlotsSelectionLiveData

    fun staffCalendar(staffCalendarRequest: JsonObject) {
        viewModelScope.launch {
            staffRepository.staffCalendar(staffCalendarRequest)
        }
    }

    private fun calculateWeekDaysList() {
        monthTitleWithYear.postValue(
            UtilsCommon.getMonthNameWithYear(
                currentCalendar.time,
                localeCode
            )
        )
        val list = arrayListOf<String>()
        for (i in 0 until noOfDayInAWeek) {
            val calendar = Calendar.getInstance(Locale(localeCode))
            calendar.time = currentCalendar.time
            calendar.add(Calendar.DATE, i)
            list.add(UtilsCommon.getFormattedDate(calendar.time, localeCode))
        }
        weekDaysListLiveData.postValue(list)
    }

    fun nextMonth(view: View) {
        currentCalendar.add(Calendar.MONTH, 1)
        currentCalendar.set(
            Calendar.DAY_OF_MONTH,
            currentCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        )
        refreshCalendar()
    }

    fun previousMonth(view: View) {
        val todayCalendar = Calendar.getInstance(Locale(localeCode))
        if (currentCalendar.get(Calendar.MONTH) <= todayCalendar.get(Calendar.MONTH) && currentCalendar.get(
                Calendar.YEAR
            ) == todayCalendar.get(Calendar.YEAR)
        )
            return
        else if ((currentCalendar.get(Calendar.MONTH) - 1) == todayCalendar.get(Calendar.MONTH) && currentCalendar.get(
                Calendar.YEAR
            ) == todayCalendar.get(Calendar.YEAR)
        ) {
            currentCalendar = todayCalendar
            refreshCalendar()
        } else {
            currentCalendar.add(Calendar.MONTH, -1)
            currentCalendar.set(
                Calendar.DAY_OF_MONTH,
                currentCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
            )
            refreshCalendar()
        }
    }

    fun nextWeek(view: View) {
        currentCalendar.add(Calendar.DAY_OF_MONTH, noOfDayInAWeek)
        refreshCalendar()
    }

    fun previousWeek(view: View) {
        val calendar = Calendar.getInstance(Locale(localeCode))
        if (calendar.getZeroTimeDate(localeCode) == currentCalendar.getZeroTimeDate(localeCode)) {
            return
        }
        currentCalendar.add(Calendar.DAY_OF_MONTH, -noOfDayInAWeek)
        if (currentCalendar.time < calendar.time) {
            currentCalendar = calendar
            refreshCalendar()
        } else {
            refreshCalendar()
        }
    }

    fun calculateTimeSlots(data: StaffCalendarModel) {
        viewModelScope.launch {
            _calculateTimeSlotsState.postValue(ResponseState.Loading())
            val list = arrayListOf<StaffCalendarTimeSlotModel>()
            val dateList = data.dateList
            val calendar = Calendar.getInstance(Locale(localeCode))
            val startDate = UtilsCommon.getDateFromTime(data.startTime ?: "00:00", localeCode)
            val endDate = UtilsCommon.getDateFromTime(data.endtime ?: "24:00", localeCode)
            calendar.time = startDate

            if (dateList.size == noOfDayInAWeek) {
                val slotBookingList = arrayListOf<SlotBookingModel>()
                var timeSlotPosition = 0
                while (calendar.time <= endDate) {

                    val timeSlotList = arrayListOf<TimeSlotModel>()
                    for (i in 0 until noOfDayInAWeek) {
                        val slotTime = UtilsCommon.getTimeFromDate(calendar.time, localeCode)
                        val bookedSlot =
                            checkBookSlots(slotTime, dateList[i].bookedSlots, dateList[i].date)
                        val timeSlotModel = TimeSlotModel(
                            time = slotTime,
                            date = dateList[i].date,
                            isChecked = bookedSlot,
                            isAvailable = checkAvailableSlots(
                                dateList[i].date,
                                slotTime,
                                dateList[i].isStaffAvailable,
                                dateList[i].unavailableSlots
                            )

                        )
                        if (bookedSlot) {
                            slotBookingList.add(
                                SlotBookingModel(
                                    timeSlotPosition,
                                    i,
                                    timeSlotModel
                                )
                            )
                        }
                        timeSlotList.add(timeSlotModel)
                    }

                    val staffCalendarTimeSlotModel = StaffCalendarTimeSlotModel(
                        time = UtilsCommon.getTimeWithAMPM(calendar.time, localeCode),
                        timeSlotList = timeSlotList
                    )
                    list.add(staffCalendarTimeSlotModel)

                    //increase time
                    calendar.add(Calendar.MINUTE, timeSlotInterval)
                    //increase time position
                    timeSlotPosition++
                }
                timeSlotListLiveData.postValue(list)
                slotBookingData = slotBookingList
            }
            _calculateTimeSlotsState.postValue(ResponseState.Success())
        }
    }

    private fun checkAvailableSlots(
        date: String?,
        slotTime: String?,
        staffAvailable: Int?,
        unavailableSlots: ArrayList<String>
    ): Boolean {
        var status = true  // true -> available  false -> unavailable
        slotTime?.let {
            if (staffAvailable == 0) {
                status = false
            } else if(checkDateIsTodayAndTimeIsOverOrNot(date,slotTime)){
                // date is equal with today and time is over
                status = false
            } else {
                first@ for (slots in unavailableSlots) {
                    val splitTime = slots.split("-", ignoreCase = true)
                    val mTimeSlots =
                        calculateTimeIntervalSlotList(splitTime.first(), splitTime.last())
                    second@ for (mTime in mTimeSlots) {
                        if (mTime.equals(slotTime, true)) {
                            status = false
                            break@first
                        }
                    }
                }
            }
        }
        return status
    }
    private fun checkDateIsTodayAndTimeIsOverOrNot(date: String?, slotTime: String?):Boolean{
        // date is equal with today and time is over
        var mStatus = false // true -> time over false -> not over
        if(date!=null && slotTime!=null) {
            val mCurrentCalendar = Calendar.getInstance(Locale(localeCode))
            val mCalCalendar = UtilsCommon.getCalendarFromDateTimeString(date,slotTime, localeCode)
            if ( mCalCalendar!=null && mCalCalendar.time<=mCurrentCalendar.time) {
                //time is over
                mStatus = true
            }
        }

        return mStatus
    }

    private fun calculateTimeIntervalSlotList(first: String, last: String): ArrayList<String> {
        val list = arrayListOf<String>()
        val startDate = UtilsCommon.getDateFromTime(first, localeCode)
        val endDate = UtilsCommon.getDateFromTime(last, localeCode)
        val calendar = Calendar.getInstance(Locale(localeCode))
        calendar.time = startDate
        while (calendar.time < endDate) {
            list.add(UtilsCommon.getTimeFromDate(calendar.time, localeCode) ?: "")
            //increase time
            calendar.add(Calendar.MINUTE, timeSlotInterval)
        }
        return list
    }

    private fun checkBookSlots(
        slotTime: String?,
        bookedSlots: ArrayList<String>,
        date: String?
    ): Boolean {
        var status = false  // true -> checked  false -> uncheck
        slotTime?.let {
            //check already added slots
            first@ for (slots in bookedSlots) {
                val splitTime = slots.split("-", ignoreCase = true)
                val mTimeSlots = calculateTimeIntervalSlotList(splitTime.first(), splitTime.last())
                second@ for (mTime in mTimeSlots) {
                    if (mTime.equals(slotTime, true)) {
                        status = true
                        break@first
                    }
                }
            }
//            // check user selected slots
//            for (item in slotBookingData) {
//                if (item.timeSlotModel.date.equals(date, true) && item.timeSlotModel.time.equals(
//                        slotTime,
//                        true
//                    )
//                ) {
//                    status = true
//                    break
//                }
//            }
        }
        return status

    }

    fun setCalendar(locale: String) {
        localeCode = locale
        currentCalendar = Calendar.getInstance(Locale(locale))
        refreshCalendar()
    }

    private fun refreshCalendar() {
        manageCalendarView()
        calculateWeekDaysList()
    }

    private fun manageCalendarView() {
        val todayCalendar = Calendar.getInstance(Locale(localeCode))
        if (currentCalendar.get(Calendar.MONTH) <= todayCalendar.get(Calendar.MONTH) && currentCalendar.get(
                Calendar.YEAR
            ) == todayCalendar.get(Calendar.YEAR)
        ){
            previousMonthVisible.postValue(false)
        }else{
            previousMonthVisible.postValue(true)
        }
        if (todayCalendar.getZeroTimeDate(localeCode) == currentCalendar.getZeroTimeDate(localeCode)) {
            previousWeekVisible.postValue(false)
        }else{
            previousWeekVisible.postValue(true)
        }
    }

    fun addToCart(addToCartRequest: JsonObject) {
        viewModelScope.launch {
            cartRepository.staffAddToCart(addToCartRequest)
        }
    }

    fun manageTimeSlotsSelection(
        slotTimePosition: Int,
        slotDatePosition: Int,
        timeSlotModel: TimeSlotModel,
    ) {
        viewModelScope.launch {
            Log.d("nTag", "manageTimeSlotsSelection: Start")
            _manageSlotsSelectionLiveData.postValue(ResponseState.Loading())

            val list = timeSlotListLiveData.value?.map {
                it.copy()
            }  //copy to a new list object; because listAdapter view is not update if same object reference submit
            list?.let {

                //deselect previous selected slots
                for (slot in slotBookingData) {
                    if (list[slot.slotTimePosition].timeSlotList[slot.slotDatePosition].isAvailable) {
                        val copyTimeSlotList = list[slot.slotTimePosition].timeSlotList.map {
                            it.copy()
                        }//copy to a new list object; because listAdapter view is not update if same object reference submit
                        copyTimeSlotList[slot.slotDatePosition].isChecked = false
                        list[slot.slotTimePosition].timeSlotList =
                            copyTimeSlotList as ArrayList<TimeSlotModel>
                    }
                }
//                for(mList in list){
//                    for(slot in mList.timeSlotList){
//                        if(slot.isAvailable){
//                            slot.isChecked=false
//                        }
//                    }
//                }

                //working for new selected slots
                val slotBookingList = arrayListOf<SlotBookingModel>()
                val nSlot = getNoOfTimeSlot(serviceTime)
                var flag = true // true - block slot present false
                for (i in slotTimePosition until (slotTimePosition + nSlot)) {
                    if (i < list.size) {
                        if (list[i].timeSlotList[slotDatePosition].isAvailable) {
                            val copyTimeSlotList = list[i].timeSlotList.map {
                                it.copy()
                            } //copy to a new list object; because listAdapter view is not update if same object reference submit
                            copyTimeSlotList[slotDatePosition].isChecked = true
                            list[i].timeSlotList = copyTimeSlotList as ArrayList<TimeSlotModel>
                            slotBookingList.add(
                                SlotBookingModel(
                                    i,
                                    slotDatePosition,
                                    list[i].timeSlotList[slotDatePosition]
                                )
                            )
                        } else {
                            flag = false
                            break
                        }
                    } else {
                        flag = false
                        break
                    }

                }

                if (flag) {
                    timeSlotListLiveData.postValue(list as ArrayList<StaffCalendarTimeSlotModel>)
                    slotBookingData = slotBookingList
                    _manageSlotsSelectionLiveData.postValue(ResponseState.Success())
                } else {
                    _manageSlotsSelectionLiveData.postValue(
                        ResponseState.Error(
                            false,
                            "This slot is not available",
                            0
                        )
                    )

                }
                Log.d("nTag", "manageTimeSlotsSelection: End")
            }
        }

    }

    private fun getNoOfTimeSlot(sTime: String): Int {
        return try {
            val times = sTime.split(" Hr")[0].split(":")
            val minutes = times[0].toInt() * 60 + times[1].toInt()
            minutes / timeSlotInterval
        } catch (e: Exception) {
            1
        }
    }

    fun setDateToCalendar(date: Date?) {

        date?.let {
            currentCalendar.time = it
            refreshCalendar()
        }
    }

    fun clearUpdateState() {
        cartRepository.clearUpdateState()
    }

}