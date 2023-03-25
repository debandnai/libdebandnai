package ie.healthylunch.app.data.model.notificationSettingsModel

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("status")
    var status: Int = 0
)