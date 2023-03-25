package ie.healthylunch.app.utils

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import ie.healthylunch.app.R

class NotificationUtils {
    companion object {
        fun createNotificationChannel(ctx:Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                //General
                val generalChannel = NotificationChannel(
                    ctx.getString(R.string.general_channel_id),
                    ctx.getString(R.string.general),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    //description = getString(R.string.other_channel_description)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    enableLights(true)
                    importance = NotificationManager.IMPORTANCE_DEFAULT
                }

                //Wallet Reminder
                /*val walletReminderChannel = NotificationChannel(
                    ctx.getString(R.string.wallet_reminder_channel_id),
                    ctx.getString(R.string.wallet_reminder),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    //description = getString(R.string.other_channel_description)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    enableLights(true)
                    importance = NotificationManager.IMPORTANCE_DEFAULT
                }*/

                //Order Reminder
                /*val orderReminderChannel = NotificationChannel(
                    ctx.getString(R.string.order_reminder_channel_id),
                    ctx.getString(R.string.order_reminder),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    //description = getString(R.string.other_channel_description)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    enableLights(true)
                    importance = NotificationManager.IMPORTANCE_DEFAULT
                }*/

                //Delete old version notification channel and group by id
                val channelId = ctx.getString(R.string.default_notification_channel_id)
                //val groupId = "other_channel_id"

                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager =
                    ctx.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.apply {
                    /*createNotificationChannelGroup(
                        NotificationChannelGroup(
                            getString(R.string.notification_channel_group_id),
                            getString(R.string.notification_channel_group_name)
                        )
                    )*/
                    deleteNotificationChannel(channelId)
                    //deleteNotificationChannelGroup(groupId)

                    //create channel
                    createNotificationChannel(generalChannel)
                    // createNotificationChannel(walletReminderChannel)
                    //createNotificationChannel(orderReminderChannel)
                }
            }
        }
    }
}