package ie.healthylunch.app.data.model.notificationListPagingModel

import com.google.gson.annotations.SerializedName

data class NotificationListResponse(
    @SerializedName("response")
    val response: Response?
)

data class Response(@SerializedName("raws") var raws: Raws? = null)
data class Raws(
    @SerializedName("success_message") var successMessage: String? = null,
    @SerializedName("data") var data: Data? = null,
    @SerializedName("publish") var publish: Publish? = null
)

data class Publish(
    @SerializedName("version") var version: String? = null,
    @SerializedName("developer") var developer: String? = null
)

data class Data(
    @SerializedName("notification_list") var notificationList: ArrayList<NotificationList>? = null,
    @SerializedName("total_count") var totalCount: Int = 0
)

data class NotificationList(

    @SerializedName("notificationid") var notificationid: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("message_param") var messageParam: MessageParam? = null,
    @SerializedName("notification_type") var notificationType: String? = null,
    @SerializedName("is_read") var isRead: String? = null,
    @SerializedName("send_to_device_type") var sendToDeviceType: Int? = null,
    @SerializedName("notification_timestamp") var notificationTimestamp: String? = null,
    @SerializedName("notification_time") var notificationTime: String? = null,
    @SerializedName("read_timestamp") var readTimestamp: String? = null

)

data class MessageParam(
    @SerializedName("data") var data: Data? = null
)