package ie.healthylunch.app.service


import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ie.healthylunch.app.R
import ie.healthylunch.app.ui.SplashActivity
import ie.healthylunch.app.utils.Constants.Companion.CAMPAIGN_ID
import ie.healthylunch.app.utils.Constants.Companion.FROM
import ie.healthylunch.app.utils.Constants.Companion.NOTIFICATION
import ie.healthylunch.app.utils.Constants.Companion.NOTIFICATION_TYPE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO

import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var SUMMERY_ID=0
    var channelID=""
    var groupKey=""
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            Log.e("onMessageReceived: ", remoteMessage.data.toString())

            if (Objects.equals(
                    MethodClass.getForeGroundActivity(this),
                    "ie.healthylunch.app.ui.NotificationActivity"
                )
            ) {

                val intent = Intent("Notification")
                intent.putExtra(CAMPAIGN_ID, remoteMessage.data["campaign_id"])
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            } else {
                if (Objects.equals(
                        MethodClass.getForeGroundActivity(this),
                        "ie.healthylunch.app.ui.DashBoardActivity"
                    )
                ) {

                    val intent = Intent("Notification")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                }

                //data {notification_type=CA, body=this is a test message !!, alert=true, image=, sound=true, title=Test 1, campaign_id=40}
                if (remoteMessage.data["image"].toString().isBlank())
                    sendNotification(
                        remoteMessage.data["title"].toString(),
                        remoteMessage.data["body"].toString(),
                        remoteMessage.data["notification_type"].toString(),
                        remoteMessage.data["campaign_id"].toString()
                    )
                else
                    prepareBitmapImageFromUrl(
                        remoteMessage.data["title"].toString(),
                        remoteMessage.data["body"].toString(),
                        remoteMessage.data["image"].toString(),
                        remoteMessage.data["notification_type"].toString(),
                        remoteMessage.data["campaign_id"].toString()
                    )

                //{"parent_id": 47838, "notification_type": "CR", "title": "Test 1", "image": "", "message": "This is a test message !!!"}

            }

        }
    }

    /*private fun getSummeryId(notificationType: String): Int {
        return if (notificationType.equals("cr1",true)){
            SUMMERY_ORDER_NOTIFICATION_ID
        } else{
            SUMMERY_OTHER_NOTIFICATION_ID
        }
    }*/

    /*private fun getChannelId(notificationType: String): String {
        return when(notificationType){
            "A", "W", "C", "WR"->{
                getString(R.string.wallet_reminder_channel_id)
            }
            "O", "OR" ->{
                getString(R.string.order_reminder_channel_id)
            }
            else ->{
                getString(R.string.general_channel_id)
            }
        }

    }*/




    override fun onNewToken(token: String) {
        super.onNewToken(token)
        UserPreferences.saveFirebaseToken(this, token)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(
        title: String,
        messageBody: String,
        bitmapImage: Bitmap,
        notificationType: String,
        campaignId: String
    ) {
        val finalTitle = if (Objects.equals(title, null) || Objects.equals(title.trim(), "null"))
            ""
        else
            title.trim()

        SUMMERY_ID= STATUS_ZERO //getSummeryId(notificationType)
        channelID=getString(R.string.general_channel_id)  //getChannelId(notificationType)
        groupKey="group_$SUMMERY_ID"

        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(FROM, NOTIFICATION)
        intent.putExtra(CAMPAIGN_ID, campaignId)
        intent.putExtra(NOTIFICATION_TYPE, notificationType)


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val notificationID = System.currentTimeMillis().toInt()
        //val channelID = getString(R.string.default_notification_channel_id)
        //val channelName: CharSequence = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(ContextCompat.getColor(this, R.color.dark_red))
            .setContentTitle(finalTitle)
            .setContentText(messageBody)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setLargeIcon(bitmapImage)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmapImage)
                    .bigLargeIcon(null)
            )
            .setGroup(groupKey)
            .setContentIntent(pendingIntent)

      /*  val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager*/

        // Since android Oreo notification channel is needed.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }*/

        //notificationManager.notify(notificationID, notificationBuilder.build())
        setSummery(channelID,title, messageBody,notificationID,notificationBuilder,groupKey,SUMMERY_ID)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(
        title: String,
        messageBody: String,
        notificationType: String,
        campaignId: String
    ) {
        val finalTitle = if (Objects.equals(title, null) || Objects.equals(title.trim(), "null"))
            ""
        else
            title.trim()

        SUMMERY_ID= STATUS_ZERO //getSummeryId(notificationType)
        channelID=getString(R.string.general_channel_id)  //getChannelId(notificationType)
        groupKey="group_$SUMMERY_ID"


        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(FROM, NOTIFICATION)
        intent.putExtra(CAMPAIGN_ID, campaignId)
        intent.putExtra(NOTIFICATION_TYPE, notificationType)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        /*val notificationManagerForAllNotification =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
       */

        val notificationID = System.currentTimeMillis().toInt()

        //val channelName: CharSequence = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(ContextCompat.getColor(this, R.color.dark_red))
            .setContentTitle(finalTitle)
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOngoing(false)
            .setAutoCancel(true)
            .setGroup(groupKey)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        /*val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager*/

        // Since android Oreo notification channel is needed.
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)

        }*/

        //notificationManager.notify(notificationID, notificationBuilder.build())
        setSummery(channelID,title, messageBody,notificationID,notificationBuilder,groupKey,SUMMERY_ID)
    }

    private fun setSummery(
        channelID: String,
        title: String,
        messageBody: String,
        notificationID: Int,
        notificationBuilder: NotificationCompat.Builder,
        GROUP_KEY: String,
        SUMMERY_ID: Int
    ) {
        val summaryNotification = NotificationCompat.Builder(this, channelID)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.notification_icon)
            .setStyle(
                NotificationCompat.InboxStyle()
                     //.addLine(title)
                     //.addLine(title)
                    // .setBigContentTitle("2 new messages")
                    .setSummaryText("")
            )

            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .build()
        //notificationManager?.notify(notificationID,notificationBuilder.build())
        NotificationManagerCompat.from(this).apply {
            notify(notificationID, notificationBuilder.build())
            notify(SUMMERY_ID, summaryNotification)
        }


    }

    private fun prepareBitmapImageFromUrl(
        title: String,
        messageBody: String,
        imgUrl: String,
        notificationType: String,
        campaignId: String
    ) {
        //ExecutorService
        val executor: ExecutorService? = Executors.newSingleThreadExecutor()
        executor?.execute {
            val inputStream: InputStream
            try {
                val url = URL(imgUrl)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.doInput = true
                httpURLConnection.connect()
                inputStream = httpURLConnection.inputStream
                val bitmapImage = BitmapFactory.decodeStream(inputStream)

                sendNotification(
                    title,
                    messageBody,
                    bitmapImage,
                    notificationType,
                    campaignId
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}

