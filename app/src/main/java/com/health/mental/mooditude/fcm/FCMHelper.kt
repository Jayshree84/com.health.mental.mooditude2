package com.health.mental.mooditude.fcm

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import java.util.*

/**
 * Created by Jayshree Rathod on 17,July,2021
 */
object FCMHelper {

    private val TAG = javaClass.simpleName
    fun setupFCMRequirements() {

        //Let's first fetch from Database
        var uniqueID = SharedPreferenceManager.getDeviceId()
        if (uniqueID == null || uniqueID.isEmpty()) {
            uniqueID = UUID.randomUUID().toString()

            //Save it to database
            SharedPreferenceManager.setDeviceId(uniqueID)
        }
        //Let's hold it in local cache
        DataHolder.instance.setDeviceId(uniqueID)

        //Let's first fetch from shared preferences
        val token = SharedPreferenceManager.getFCMToken()
        if (token != null && token.isNotEmpty()) {
            debugLog(TAG, "FCM Token found from Database : " + token)

            //Set is to DataHolder
            DataHolder.instance.setFCMToken(token)

        } else {
            //Query for token
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    errorLog(TAG, "Fetching FCM registration token failed " + task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val fcmToken = task.result
                debugLog(TAG, "New token found :: " + fcmToken)

                if (fcmToken != null) {
                    //New token found
                    //Save it to database
                    SharedPreferenceManager.setFCMToken(fcmToken)
                    DataHolder.instance.setFCMToken(fcmToken)

                    //Pass it to Firebase database too
                    DBManager.instance.setFcmToken(fcmToken)
                }
            })
        }
    }

}