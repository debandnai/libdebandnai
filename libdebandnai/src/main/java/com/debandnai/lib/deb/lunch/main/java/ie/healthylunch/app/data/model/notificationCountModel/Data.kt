package ie.healthylunch.app.data.model.notificationCountModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("notificationcount")
                val notificationcount: Int = 0)