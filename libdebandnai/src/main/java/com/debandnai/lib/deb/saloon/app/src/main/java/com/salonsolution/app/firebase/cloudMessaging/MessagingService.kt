package com.salonsolution.app.firebase.cloudMessaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.firebase.cloudMessaging.NotificationUtils.handleDataMessage

class MessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //store fcm token
        Log.d("tag","onNewToken $token")
        AppSettingsPref(this).setFCMToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        when {
            message.notification!=null && message.data.isNotEmpty() -> {
                // notification payload and data payload present
                //when app in background, fcm handle it. get data in Launcher activity
                // when app in foreground, you need to handle it
                Log.d("tag","notification payload and data payload present")
                handleDataMessage(applicationContext,message.notification,message.data)
            }
            message.notification!=null -> {
                // only notification payload present
                //when app in background, fcm handle it.
                // when app in foreground, you need to handle it
                Log.d("tag"," notification payload present")
                handleDataMessage(applicationContext,message.notification,null)
            }
            message.data.isNotEmpty() -> {
                //only data payload present
                //app in background/ foreground,you need to handle it.
                Log.d("tag","only data payload present")
                 handleDataMessage(applicationContext, null, message.data)
                 }
            else -> {
                //no data and notification payload
                Log.d("tag","No data and notification")
            }
        }
    }

}