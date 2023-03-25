package ie.healthylunch.app.data.model.quickViewOrderDayModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("day_details")
                val dayDetails: DayDetails,
                @SerializedName("calendar_disable_time")
                val calendar_disable_time: String?=null,
                @SerializedName("student_list")
                val studentList: List<StudentListItem>?)