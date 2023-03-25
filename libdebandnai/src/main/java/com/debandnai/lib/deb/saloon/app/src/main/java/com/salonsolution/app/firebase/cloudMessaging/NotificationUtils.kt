package com.salonsolution.app.firebase.cloudMessaging

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.RemoteMessage
import com.salonsolution.app.R
import com.salonsolution.app.ui.activity.SplashActivity
import com.salonsolution.app.utils.Constants


object NotificationUtils {

    private const val GENERAL_NOTIFICATION_ID = 100
    private const val PRODUCT_PROMOTION_NOTIFICATION_ID = 200
    private const val ORDER_UPDATES_NOTIFICATION_ID = 300
    private const val NOTIFICATION_ID_MAX_LIMIT = 100
    private var NOTIFICATION_ID = 0
    private val generalInboxStyle = NotificationCompat.InboxStyle()
    private val orderInboxStyle = NotificationCompat.InboxStyle()
    private val productInboxStyle = NotificationCompat.InboxStyle()

    fun handleDataMessage(
        context: Context,
        notification: RemoteMessage.Notification?,
        data: Map<String, String>?
    ) {
        val intent = Intent(context, SplashActivity::class.java)
        intent.putExtra(Constants.FROM,Constants.NOTIFICATION)
        intent.putExtra(Constants.NOTIFICATION_TYPE, data?.get(Constants.NOTIFICATION_TYPE) ?:"")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        var channelId = notification?.channelId?: context.getString(R.string.general_channel_id)
        if (!data.isNullOrEmpty()) {
            when (data[Constants.NOTIFICATION_TYPE]) {
                Constants.ORDER_REMAINDER -> {
                    channelId = context.getString(R.string.order_updates_channel_id)
                    showNotificationMessage(context, notification, intent,channelId)
                }
                Constants.PRODUCT_PROMOTION -> {
                    channelId = context.getString(R.string.product_promotion_channel_id)
                    showNotificationMessage(context, notification, intent,channelId)
                }
                else -> {
                    showNotificationMessage(context, notification, intent,  channelId)
                }
            }
        } else {
            showNotificationMessage(context, notification, intent,  channelId)
        }


    }

    private fun showNotificationMessage(
        context: Context,
        notification: RemoteMessage.Notification?,
        intent: Intent,
        channelId: String?
    ) {
//        val resultPendingIntent = TaskStackBuilder.create(context).run {
//            // Add the intent, which inflates the back stack
//            addNextIntentWithParentStack(intent)
//            // Get the PendingIntent containing the entire back stack
//            getPendingIntent(
//                0,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        }

        val resultPendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (notification?.imageUrl==null)
            showSmallNotification(context, notification, resultPendingIntent, channelId)
        else
            showBigNotification(context, notification, resultPendingIntent, channelId)
    }

    private fun showBigNotification(
        context: Context,
        notification: RemoteMessage.Notification?,
        pendingIntent: PendingIntent,
        channelId: String?
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mChannelId = channelId ?: context.getString(R.string.general_channel_id)
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(notification?.title)
        bigPictureStyle.setSummaryText(
            HtmlCompat.fromHtml(
                notification?.body.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString()
        )
        if (NOTIFICATION_ID >= NOTIFICATION_ID_MAX_LIMIT) {
            NOTIFICATION_ID = 0
        } else {
            NOTIFICATION_ID++
        }

        val builder = NotificationCompat.Builder(context, mChannelId)
            .setAutoCancel(true)
            .setTicker(notification?.title)
            .setContentTitle(notification?.title)
            .setContentText(notification?.body)
            .setWhen(notification?.eventTime?:System.currentTimeMillis())
            .setPriority(notification?.notificationPriority?:NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.FLAG_SHOW_LIGHTS)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_icon))
            .setColor(ContextCompat.getColor(context, R.color.brand_color))
            .setContentIntent(pendingIntent)



        Glide.with(context)
            .asBitmap()
            .load(notification?.imageUrl)
            .into(object : CustomTarget<Bitmap?>() {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    bigPictureStyle.bigPicture(resource)
                    builder.setStyle(bigPictureStyle)
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                }


                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    private fun showSmallNotification(
        context: Context,
        notification: RemoteMessage.Notification?,
        pendingIntent: PendingIntent,
        channelId: String?
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mChannelId = channelId ?: context.getString(R.string.general_channel_id)
        if (NOTIFICATION_ID >= NOTIFICATION_ID_MAX_LIMIT) {
            NOTIFICATION_ID = 0
        } else {
            NOTIFICATION_ID++
        }


        val notificationGroupId = getNotificationIdByChannelId(context,channelId)
        val notificationBuilder = NotificationCompat.Builder(context, mChannelId)
            .setAutoCancel(true)
            .setGroup("group_$notificationGroupId")
            .setTicker(notification?.title)
            .setContentTitle(notification?.title)
            .setContentText(notification?.body)
            .setWhen(notification?.eventTime?:System.currentTimeMillis())
            .setStyle(NotificationCompat.BigTextStyle())
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ContextCompat.getColor(context, R.color.brand_color))
            .setPriority(notification?.notificationPriority?:NotificationCompat.PRIORITY_HIGH)
            .setLights(Color.RED, 500, 1)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.FLAG_SHOW_LIGHTS)
            .setContentIntent(pendingIntent)

        ////build summary info into InboxStyle template
        val inboxStyle = getInboxStyleByNotificationId(notificationGroupId)
        inboxStyle.addLine("${notification?.title} ${notification?.body}")
            .setBigContentTitle(context.getString(R.string.app_name))
//            .setSummaryText(context.getString(R.string.general_channel_id))

        val summaryBuilder = NotificationCompat.Builder(context, mChannelId)
            .setAutoCancel(true)
            .setGroup("group_$notificationGroupId")
            .setGroupSummary(true)
            .setContentTitle(notification?.title)
            //set content text to support devices running API level < 24
            .setContentText(context.getString(R.string.app_name))
            .setWhen(notification?.eventTime?:System.currentTimeMillis())
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setStyle(inboxStyle)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ContextCompat.getColor(context, R.color.brand_color))
            .setPriority(notification?.notificationPriority?:NotificationCompat.PRIORITY_HIGH)
            .setLights(Color.GREEN, 500, 1)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.FLAG_SHOW_LIGHTS)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        notificationManager.notify(notificationGroupId, summaryBuilder.build())
    }

    private fun getNotificationIdByChannelId(context: Context, channelId: String?): Int {
        return when {
            channelId.equals(context.getString(R.string.order_updates_channel_id)) -> {
                ORDER_UPDATES_NOTIFICATION_ID
            }
            channelId.equals(context.getString(R.string.product_promotion_channel_id)) -> {
                PRODUCT_PROMOTION_NOTIFICATION_ID
            }
            else -> {
                GENERAL_NOTIFICATION_ID
            }
        }
    }

    private fun getInboxStyleByNotificationId(notificationId: Int): NotificationCompat.InboxStyle {
        return when (notificationId) {
            ORDER_UPDATES_NOTIFICATION_ID -> {
                orderInboxStyle
            }
            PRODUCT_PROMOTION_NOTIFICATION_ID -> {
                productInboxStyle
            }

            else -> {
                generalInboxStyle
            }
        }
    }


    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            //General
            val generalChannel = NotificationChannel(
                context.getString(R.string.general_channel_id),
                context.getString(R.string.general_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.general_channel_description)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableLights(true)
                enableVibration(true)
                lightColor = Color.RED
                importance = NotificationManager.IMPORTANCE_HIGH
            }


            //Order updates
            val orderUpdatesChannel = NotificationChannel(
                context.getString(R.string.order_updates_channel_id),
                context.getString(R.string.order_updates_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.order_updates_channel_description)
                group = context.getString(R.string.notification_channel_orders_group_id)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableLights(true)
                enableVibration(true)
                importance = NotificationManager.IMPORTANCE_HIGH
            }

            //Product promotion
            val productPromotionChannel = NotificationChannel(
                context.getString(R.string.product_promotion_channel_id),
                context.getString(R.string.product_promotion_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.product_promotion_channel_description)
                group = context.getString(R.string.notification_channel_promotion_group_id)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableLights(true)
                enableVibration(true)
                importance = NotificationManager.IMPORTANCE_HIGH
            }

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.apply {
                //orders group
                createNotificationChannelGroup(
                    NotificationChannelGroup(
                        context.getString(R.string.notification_channel_orders_group_id),
                        context.getString(R.string.notification_channel_orders_group_name)
                    )
                )

                //promotion group
                createNotificationChannelGroup(
                    NotificationChannelGroup(
                        context.getString(R.string.notification_channel_promotion_group_id),
                        context.getString(R.string.notification_channel_promotion_group_name)
                    )
                )

                createNotificationChannel(generalChannel)
                createNotificationChannel(orderUpdatesChannel)
                createNotificationChannel(productPromotionChannel)
            }
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!manager.areNotificationsEnabled()) {
                return false
            }
            for (channel in manager.notificationChannels) {
                if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                    return false
                }
            }
            true
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    // Clears notification tray messages
    fun clearNotifications(context: Context?) {
        context?.let {
            val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }
    }
}