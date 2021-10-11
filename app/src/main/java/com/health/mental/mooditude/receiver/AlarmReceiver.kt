package com.health.mental.mooditude.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.SplashActivity
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.ALARM_ID_FOR_USER_REMINDER
import com.health.mental.mooditude.utils.NotificationUtils


/**
 * Created by Jayshree.Rathod on 10-10-2017.
 */
class AlarmReceiver : BroadcastReceiver() {

    //used for logging purpose
    protected val TAG = this.javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        debugLog(TAG, "Alarm received : ")
        debugLog(TAG, "Alarm received  for extras : " + intent.extras +
                " : Bundle : " + intent.getBundleExtra("bundle"))

        if (intent.getBundleExtra("bundle") != null) {
            val bundle = intent.getBundleExtra("bundle")!!
            debugLog(TAG, "intent.extras :with keys :  " + intent.extras!!.keySet().toString())

            //first check for scorecard
            if (bundle.getBoolean("reminder", false)) {
                showReminderAlert(context)
                return
            }
        }
    }


    /**
     * Score card alert - It should be displayed even application is running and in foreground
     */
    private fun showReminderAlert(context: Context) {

        val channelId = NotificationUtils.registerLocationNotifChnnl(context)

        //add notification
        val resultIntent = Intent(context, SplashActivity::class.java)

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        val resultPendingIntent = PendingIntent.getActivity(
                context,
                ALARM_ID_FOR_USER_REMINDER,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        NotificationUtils.sendNotification(context, context.getString(R.string.how_you_feel_today),
                context.getString(R.string.check_in), channelId,
                resultPendingIntent, ALARM_ID_FOR_USER_REMINDER)

    }

}