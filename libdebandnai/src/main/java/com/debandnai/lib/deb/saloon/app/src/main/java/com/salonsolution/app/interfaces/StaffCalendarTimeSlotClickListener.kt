package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.TimeSlotModel

interface StaffCalendarTimeSlotClickListener {
    fun onClick(slotTimePosition:Int,slotDatePosition:Int, timeSlotModel: TimeSlotModel)
}