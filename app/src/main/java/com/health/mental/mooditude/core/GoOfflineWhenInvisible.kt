package com.health.mental.mooditude.core

/**
 * Created by Jayshree.Rathod on 12-03-2018.
 */

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

/**
 * When no activities are visible in this application, tell Firebase Realtime
 * Database to go offline.  This will help reduce the number of concurrent
 * connections to the database when the app is not being used.  Without this,
 * Firebase will retain a connection to the database when the app is invisible
 * and the process is still alive (Android will retain app processes as an
 * optimization until its resources are needed).  This may also save data
 * usage while the app is not being used if you use keepSynced() or long-lived
 * listeners at database locations (this app does).
 *
 *
 * Here, we go offline when all Activities have been stopped for at least 30
 * seconds, then reconnect when the next Activity is started.
 */

class GoOfflineWhenInvisible(private val fdb: FirebaseDatabase) : Application.ActivityLifecycleCallbacks {

    companion object {
        private val OFFLINE_DELAY = TimeUnit.SECONDS.toMillis(30)
    }

    private val handler = Handler()
    private var numActivitiesStarted: Int = 0

    private val goOffline = Runnable {
        println("Going offline now")
        fdb.goOffline()
    }

    private val goOnline = Runnable { fdb.goOnline() }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        if (numActivitiesStarted == 0) {
            println("Only activity started, going online")
            handler.removeCallbacks(goOffline)
            fdb.goOnline()
        }
        numActivitiesStarted++
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        numActivitiesStarted--
        if (numActivitiesStarted == 0) {
            println("Last activity stopped, going offline")
            handler.postDelayed(goOffline, OFFLINE_DELAY)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

}