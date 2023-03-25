package com.salonsolution.app.ui.base

import android.app.Application
import com.salonsolution.app.firebase.cloudMessaging.NotificationUtils.createNotificationChannel
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SalonApplication: Application() {
}