package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class NotificationListModel(
    @SerializedName("notification_list") var notificationList: ArrayList<NotificationList> = arrayListOf()
)

data class NotificationList(
    @SerializedName("notification_id") var notificationId: Int? = null,
    @SerializedName("notification_message") var notificationMessage: String? = null,
    @SerializedName("notification_date") var notificationDate: String? = null,
    @SerializedName("notification_time") var notificationTime: String? = null
)
