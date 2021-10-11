package com.health.mental.mooditude

import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.health.mental.mooditude.services.freshchat.ChatService
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.fcm.FCMHelper
import com.health.mental.mooditude.services.instrumentation.EventCatalog

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
class MooditudeApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        /**
         * From API 18 to 23, android does not check for file uri exposure by default. Calling this method enables this check.
         * From API 24, android does this check by default. But we can disable it by setting a new VmPolicy
         */
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        //Enable collection for selected users by initializing Crashlytics from one of your app's activities
        /**
         * Note: You can't stop Crashlytics reporting once you've initialized it in an app session.
         * To opt-out of reporting after you've initialized Crashlytics, users have to restart your app.
         */

        //Enable crash reporting
        //Fabric.with(this, Crashlytics())

        //Disable crash reporting
        //configureCrashReporting()

        initApplication()

        //Initialize Analytics to collect user events
        //AppAnalytics.instance.initAnalytics(this)
    }

    private fun initApplication() {
        //init preferences
        SharedPreferenceManager.with(this)

        //Setup FCM requirements
        FCMHelper.setupFCMRequirements()

        //first initialize app database
        DBManager.createManager(this)

        //Init FreshChat
        ChatService.createService(this)

        //Init catalog
        EventCatalog.createService(this)
    }


    /*private fun configureCrashReporting() {
        val crashlyticsCore = CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build())
    }
*/
}
