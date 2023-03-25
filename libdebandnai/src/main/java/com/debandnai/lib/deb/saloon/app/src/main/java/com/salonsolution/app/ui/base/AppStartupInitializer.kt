package com.salonsolution.app.ui.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import com.salonsolution.app.firebase.cloudMessaging.NotificationUtils.createNotificationChannel
import com.salonsolution.app.utils.ContextUtils
import com.salonsolution.app.utils.SettingsUtils


class AppStartupInitializer : Initializer<Unit> {


    @SuppressLint("MissingPermission")
    override fun create(context: Context) {
        //create notification channel
        createNotificationChannel(context)
        //Set Light theme
        SettingsUtils.setAppTheme("1")
        //run locale migration
        SettingsUtils.runLocaleMigration(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}