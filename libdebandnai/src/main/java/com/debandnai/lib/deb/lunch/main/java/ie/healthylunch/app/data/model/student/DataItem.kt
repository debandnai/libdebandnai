package ie.healthylunch.app.data.model.student

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("studentid")
    val studentid: Int = 0,
    @SerializedName("school")
    val school: String = "",
    @SerializedName("l_name")
    val lName: String = "",
    @SerializedName("f_name")
    val fName: String = "",
    @SerializedName("class")
    val studentClass: String = "",
    var isSelected: Boolean = false
)