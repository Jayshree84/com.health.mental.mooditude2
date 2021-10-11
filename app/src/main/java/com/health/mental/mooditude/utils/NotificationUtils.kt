package com.health.mental.mooditude.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.SplashActivity
import org.jetbrains.anko.runOnUiThread


/**
 * Created by Jayshree.Rathod on 18-01-2018.
 */
object NotificationUtils {

    private val NOTIFICATION_CHANNEL_ID = "com_health_mental_mooditude_channel_one"
    private val STATUSBAR_NOTIFICATION_CHANNEL_ID = "com_health_mental_mooditude_status_bar"


    fun shouldShowNotification(context: Context): Boolean {
        val myProcess = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)
        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true

        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        // app is in foreground, but if screen is locked show notification anyway
        return km.inKeyguardRestrictedInputMode()
    }


    fun registerLocationNotifChnnl(context: Context): String {
        if (Build.VERSION.SDK_INT >= 26) {
            val mngr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mngr.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) {
                return NOTIFICATION_CHANNEL_ID
            }
            //
            val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "notification_mooditude",
                    NotificationManager.IMPORTANCE_HIGH)
            // Configure the notification channel.
            channel.description = ""
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            channel.enableVibration(false)
            channel.canShowBadge()
            channel.setShowBadge(true)
            mngr.createNotificationChannel(channel)
        }

        return NOTIFICATION_CHANNEL_ID
    }


    fun registerLocationNotifChnnlForStatusBar(context: Context): String {
        if (Build.VERSION.SDK_INT >= 26) {
            val mngr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mngr.getNotificationChannel(STATUSBAR_NOTIFICATION_CHANNEL_ID) != null) {
                return STATUSBAR_NOTIFICATION_CHANNEL_ID
            }

            val channel = NotificationChannel(
                    STATUSBAR_NOTIFICATION_CHANNEL_ID,
                    "Mooditude Notifications",
                    NotificationManager.IMPORTANCE_MIN)
            // Configure the notification channel.
            channel.canShowBadge()
            channel.setShowBadge(false)
            channel.canBypassDnd()
            channel.setBypassDnd(true)
            mngr.createNotificationChannel(channel)
        }

        return STATUSBAR_NOTIFICATION_CHANNEL_ID
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    fun sendNotification(context: Context, title: String, message: String, channelId: String,
                         pendingIntent: PendingIntent, notificationID: Int) {

        //val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= 26) {
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(ContextCompat.getColor(context, R.color.brand_yellow))
                    .setContentIntent(pendingIntent)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build())

        } else {
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setColor(ContextCompat.getColor(context, R.color.white))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build())
        }
    }


    fun sendNotificationMessage(context: Context, title: String, message: String, profileImage: String, channelId: String,
                                pendingIntent: PendingIntent, /*ticketId: String, notificationType: String,*/ notificationID: Int) {

        //val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 1. Build RemoteInput for Reply option
       /* val replyLabel = context.getString(R.string.reply)
        val remoteInput = RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build()*/

        // 2. Build action
        /*val replyAction = NotificationCompat.Action.Builder(
                0, replyLabel, getNotificationPendingIntent(context, notificationID, ticketId, REPLY_ACTION))
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()

        var acceptAction = NotificationCompat.Action.Builder(
                0, context.getString(R.string.accept), getNotificationPendingIntent(context, notificationID, ticketId, ACCEPT_ACTION))
                .build()

        var declineAction = NotificationCompat.Action.Builder(
                0, context.getString(R.string.decline), getNotificationPendingIntent(context, notificationID, ticketId, REJECT_ACTION))
                .build()*/

        var notificationBuilder: NotificationCompat.Builder? = null

        if (Build.VERSION.SDK_INT >= 26) {

            notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.status_bar_icon)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(ContextCompat.getColor(context, R.color.brand_yellow))
                    .setContentIntent(pendingIntent)

        } else {

            notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.status_bar_icon)
                    .setColor(ContextCompat.getColor(context, R.color.brand_yellow))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
        }

        /*if (notificationType.equals(NotificationMessage.NotificationMessageType.Assignment.toString())) {
            notificationBuilder!!.addAction(declineAction)
            notificationBuilder.addAction(acceptAction)

        } else if (notificationType.equals(NotificationMessage.NotificationMessageType.TicketCommentAdded.toString())) {
            notificationBuilder!!.addAction(replyAction)
        }*/

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build())

        context.runOnUiThread {
            //UiUtils.setLargeIconForNotification(context, profileImage, notificationBuilder, notificationManager, notificationID)
        }
    }

    fun addNotificationInStatusbar(context: Context) {
        val channelId = registerLocationNotifChnnlForStatusBar(context)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent1 = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent1,
                PendingIntent.FLAG_ONE_SHOT)
        var notification: Notification? = null
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Mooditude")
                .setContentText("Launch the app")
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.status_bar_icon)
                .setColor(ContextCompat.getColor(context, R.color.brand_yellow))
        notification = notificationBuilder.build()

        notification.flags = notification.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1 /* ID of notification */, notification)
    }


    /*
    private fun getNotificationPendingIntent(context: Context, mNotificationId: Int, ticketId: String, action: String): PendingIntent {
        Log.d("comment_data", "Ticket ID ${ticketId}")

        val intent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = getActionIntent(context, mNotificationId, ticketId, action)
            return PendingIntent.getBroadcast(context, 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            // start your activity
            intent = getActionIntent(context, mNotificationId, ticketId, action)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }


    private fun getActionIntent(context: Context, notificationId: Int, ticketID: String, action: String): Intent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        when {
            action.equals(REPLY_ACTION) -> {
                intent.action = REPLY_ACTION
            }
            action.equals(REJECT_ACTION) -> {
                intent.action = REJECT_ACTION
            }
            action.equals(ACCEPT_ACTION) -> {
                intent.action = ACCEPT_ACTION
            }
        }

        intent.putExtra(KEY_NOTIFICATION_ID, notificationId)
        intent.putExtra(KEY_TICKET_ID, ticketID)
        return intent
    }
    */


}