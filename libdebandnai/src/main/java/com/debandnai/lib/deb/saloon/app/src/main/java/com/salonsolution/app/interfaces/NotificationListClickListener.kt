package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.NotificationList

interface NotificationListClickListener {
    fun onDeleteClick(position: Int, item: NotificationList)
    fun onItemClick(position: Int, item: NotificationList)
}