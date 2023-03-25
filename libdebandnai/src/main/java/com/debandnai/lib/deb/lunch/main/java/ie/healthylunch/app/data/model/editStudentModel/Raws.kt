package ie.healthylunch.app.data.model.editStudentModel

import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.data.model.baseModel.Publish

data class Raws(@SerializedName("success_message")
                val successMessage: String = "",
                @SerializedName("publish")
                val publish: Publish)