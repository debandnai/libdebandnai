package ie.healthylunch.app.data.model.studentListModel

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("studentid") val studentid: Int = 0,
    @SerializedName("school") val school: String = "",
    @SerializedName("dob") val dob: String = "",
    @SerializedName("l_name") val lName: String = "",
    @SerializedName("f_name") val fName: String = "",
    @SerializedName("county") val county: String = "",
    @SerializedName("class") val student_class: String = "",
    @SerializedName("user_type") val user_type: String = "",
    @SerializedName("unique_school_name") val uniqueSchoolName: String = "",
    @SerializedName("xp_points") val xpPoints: Int? = 0,
    @SerializedName("school_type") val schoolType: Int? = 0,
    @SerializedName("is_edited") val isEdited: Int? = 0,
    @SerializedName("is_deactivate") val isDeactivate: Int? = 0,
    var isSelected: Boolean = false
)