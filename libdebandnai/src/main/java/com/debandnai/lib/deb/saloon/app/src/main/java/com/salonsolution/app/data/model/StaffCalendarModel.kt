package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class StaffCalendarModel(
    @SerializedName("service_id") var serviceId: Int? = null,
    @SerializedName("start_time") var startTime: String? = null,
    @SerializedName("endtime") var endtime: String? = null,
    @SerializedName("date_list") var dateList: ArrayList<DateList> = arrayListOf()
)

data class DateList(

    @SerializedName("date") var date: String? = null,
    @SerializedName("is_staff_available") var isStaffAvailable: Int? = null, // 0-> Unavailable  1-> Available
    @SerializedName("booked_slots") var bookedSlots: ArrayList<String> = arrayListOf(),
    @SerializedName("unavailable_slots") var unavailableSlots: ArrayList<String> = arrayListOf()

)

data class WeekDaysModel(
    var date: String? = null,
    var dayName: String? = null,
    var isSelected: Boolean = false
)

data class StaffCalendarTimeSlotModel(
    var time: String? = null,
    var timeSlotList: ArrayList<TimeSlotModel> = arrayListOf()
)

data class TimeSlotModel(
    var time: String? = null,
    var date: String? = null,
    var isChecked: Boolean = false,
    var isAvailable: Boolean = true
)

data class SlotBookingModel(
    var slotTimePosition: Int,
    var slotDatePosition: Int,
    var timeSlotModel: TimeSlotModel
)