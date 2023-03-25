package ie.healthylunch.app.data.model.quickViewOrderDayModel

import com.google.gson.annotations.SerializedName

data class StudentListItem(
    @SerializedName("order_menu")
    val orderMenu: Int = 0,
    @SerializedName("user_type")
    val userType: String = "",
    @SerializedName("l_name")
    val lName: String = "",
    @SerializedName("f_name")
    val fName: String = "",
    @SerializedName("student_id")
    val studentId: Int = 0,
    @SerializedName("order_msg_heading")
    val orderMsgHeading: String = "",
    @SerializedName("order_msg_icon")
    val orderMsgIcon: Int? = 0,
    @SerializedName("order_msg_description")
    val orderMsgDescription: String? = null
)