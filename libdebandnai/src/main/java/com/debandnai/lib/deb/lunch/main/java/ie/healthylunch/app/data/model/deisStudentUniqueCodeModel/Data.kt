package ie.healthylunch.app.data.model.deisStudentUniqueCodeModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
    @SerializedName("user_type") val userType: String? = null,
    @SerializedName("school_address") val schoolAddress: String? = null,
    @SerializedName("Student_id") val studentId: Int? = 0,
    @SerializedName("class_id") val classId: Int? = 0,
    @SerializedName("l_name") val lName: String? = null,
    @SerializedName("f_name") val fName: String? = null,
    @SerializedName("school_name") val schoolName: String? = null,
    @SerializedName("class_name") val className: String? = null
) : Parcelable