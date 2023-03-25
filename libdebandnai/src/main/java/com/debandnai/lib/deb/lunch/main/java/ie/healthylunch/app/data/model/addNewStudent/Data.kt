package ie.healthylunch.app.data.model.addNewStudent

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("user_type")
    val userType: String = "",
    @SerializedName("Student_id")
    val studentId: Int = 0

)


