package ie.healthylunch.app.data.model.quickViewModel

import com.google.gson.annotations.SerializedName

data class StudentListItem(@SerializedName("order_menu")
                           val orderMenu: Int = 0,
                           @SerializedName("l_name")
                           val lName: String = "",
                           @SerializedName("f_name")
                           val fName: String = "",
                           @SerializedName("student_id")
                           val studentId: Int = 0,
                           @SerializedName("order_msg")
                           val orderMsg: String = "")