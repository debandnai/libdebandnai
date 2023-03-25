package ie.healthylunch.app.data.model.notificationSettingsModel

import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.data.model.baseModel.Publish

data class Raws(@SerializedName("data")
                val data: List<DataItem>?,
                @SerializedName("success_message")
                val successMessage: String = "",
                @SerializedName("publish")
                val publish: Publish)