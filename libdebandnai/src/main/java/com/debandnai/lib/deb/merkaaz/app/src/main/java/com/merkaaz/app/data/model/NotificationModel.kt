package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("notification_list") val notificationList: List<NotificationItem?>?
)
data class NotificationItem(
    @SerializedName("notification_date") val notificationDate: String? = null,
    @SerializedName("notification_id") val notificationId: Int? = 0,
    @SerializedName("notification_content") val notificationContent: String? = null
)