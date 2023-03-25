package ie.healthylunch.app.data.model.notificationListModel

import com.google.gson.annotations.SerializedName

data class MessageParam(@SerializedName("data")
                        val data: Data)