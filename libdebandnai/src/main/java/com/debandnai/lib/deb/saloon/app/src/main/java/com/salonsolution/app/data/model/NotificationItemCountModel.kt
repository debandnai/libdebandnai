package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class NotificationItemCountModel(
    @SerializedName("notification_count") var notificationCount: Int? = null,
)
