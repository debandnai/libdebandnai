package ie.healthylunch.app.data.model.quickViewModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("day_details")
                val dayDetails: DayDetails,
                @SerializedName("student_list")
                val studentList: List<StudentListItem>?)