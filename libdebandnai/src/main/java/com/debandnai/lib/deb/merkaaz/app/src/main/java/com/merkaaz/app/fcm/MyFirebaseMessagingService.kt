package com.merkaaz.app.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.merkaaz.app.R
import com.merkaaz.app.ui.activity.NotificationsActivity
import com.merkaaz.app.ui.activity.NotificationsActivity_GeneratedInjector
import com.merkaaz.app.utils.Constants.APPROVED_USER
import com.merkaaz.app.utils.SharedPreff
import com.merkaaz.app.utils.UserApprovedBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService(){

    @Inject
    lateinit var sharedPreff: SharedPreff

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("fcm token...", "onNewToken: $token")
        println("fcm token...$token")
        sharedPreff.setfirebase_token(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("message", "$message")
        println("message....."+ message.data["notification_type"])
        if (message.data.isNotEmpty()) {
            when(message.data["notification_type"].toString().trim()){
                "CA"->{
                    //val intent = Intent(APPROVED_USER)
                    //LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    sendBroadcast(Intent(applicationContext, UserApprovedBroadcastReceiver::class.java).setAction(APPROVED_USER))
                }
                else->{
                    sendNotification(message.data["title"].toString(),message.data["body"].toString(),message.data["notification_type"].toString())
                }
            }

        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(
        title: String,
        messageBody: String,
        notificationType: String
    ) {


        val intent = Intent(this, NotificationsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val notificationID = System.currentTimeMillis().toInt()
        val channelID = getString(R.string.default_notification_channel_id)
        val channelName: CharSequence = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setColor(ContextCompat.getColor(this, R.color.teal_700))
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationID, notificationBuilder.build())

    }
}