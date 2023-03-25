package ie.healthylunch.app.data.model.notificationListModel

import com.google.gson.annotations.SerializedName

data class DataItem(@SerializedName("notification_type")
                    val notificationType: String = "",
                    @SerializedName("is_read")
                    val isRead: String = "",
                    @SerializedName("is_new")
                    val isNew: String = "",
                    @SerializedName("notification_sub_type")
                    val notificationSubType: Int = 0,
                    @SerializedName("send_to_device_type")
                    val sendToDeviceType: Int = 0,
                    @SerializedName("notification_timestamp")
                    val notificationTimestamp: String = "",
                    @SerializedName("read_timestamp")
                    val readTimestamp: String = "",
                    @SerializedName("notificationid")
                    val notificationid: Int = 0,
                    @SerializedName("message")
                    val message: String = "",
                    @SerializedName("message_param")
                    val messageParam: MessageParam)