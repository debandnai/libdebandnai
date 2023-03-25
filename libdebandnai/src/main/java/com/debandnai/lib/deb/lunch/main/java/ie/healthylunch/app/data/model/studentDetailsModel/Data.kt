package ie.healthylunch.app.data.model.studentDetailsModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("school_id")
                val schoolId: Int = 0,
                @SerializedName("school")
                val school: String = "",
                @SerializedName("dob")
                val dob: String = "",
                @SerializedName("l_name")
                val lName: String = "",
                @SerializedName("class_id")
                val classId: Int = 0,
                @SerializedName("f_name")
                val fName: String = "",
                @SerializedName("county")
                val county: String = "",
                @SerializedName("student_id")
                val studentId: Int = 0,
                @SerializedName("class_name")
                val className: String = "") {
                var isSelected: Boolean=false
}