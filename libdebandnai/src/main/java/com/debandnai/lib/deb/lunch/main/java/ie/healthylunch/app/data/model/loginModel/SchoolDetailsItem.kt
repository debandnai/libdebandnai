package ie.healthylunch.app.data.model.loginModel

import com.google.gson.annotations.SerializedName

data class SchoolDetailsItem(@SerializedName("school_name")
                             val schoolName: String = "",
                             @SerializedName("unique_school_name")
                             val uniqueSchoolName: String = "",
                             @SerializedName("id")
                             val id: Int = 0)