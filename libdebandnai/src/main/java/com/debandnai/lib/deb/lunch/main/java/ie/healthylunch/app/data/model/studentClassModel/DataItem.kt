package ie.healthylunch.app.data.model.studentClassModel

import com.google.gson.annotations.SerializedName

data class DataItem(@SerializedName("class_id")
                    val classId: Int = 0,
                    @SerializedName("class_name")
                    val className: String = "") {
    var isSelected: Boolean=false
}