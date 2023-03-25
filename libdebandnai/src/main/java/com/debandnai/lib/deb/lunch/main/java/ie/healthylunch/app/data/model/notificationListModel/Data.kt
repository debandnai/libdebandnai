package ie.healthylunch.app.data.model.notificationListModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("notification_type")
                val notificationType: String = "",
                @SerializedName("badge")
                val badge: String = "",
                @SerializedName("image")
                val image: String = "",
                @SerializedName("alert")
                val alert: String = "",
                @SerializedName("sound")
                val sound: String = "",
                @SerializedName("vibrate")
                val vibrate: String = "",
                @SerializedName("title")
                val title: String = "",
                @SerializedName("message")
                val message: String = "")