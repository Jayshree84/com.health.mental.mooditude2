package com.health.mental.mooditude

/**
 * Created by Jayshree Rathod on 03,July,2021
 */
import android.util.Log


/**
 * Created by Jayshree.Rathod on 24-08-2017.
 */
/*fun printLog(logText: String) {
    if (BuildConfig.DEBUG) {
        System.out.println("==>> " + logText)
    }
    //Crashlytics.log(1, "InfoLog", logText);
}


fun printDebugLog(logText: String) {
    Log.d( "DEBUG_LOG", logText)
}*/

fun debugLog(tagText:String, logText:String) {
    if (BuildConfig.DEBUG) {
        Log.d(tagText, logText)
    }
}

fun warnLog(tagText:String, logText:String) {
    if (BuildConfig.DEBUG) {
        Log.w(tagText, logText)
    }
}

fun errorLog(tagText:String, logText:String) {
    if (BuildConfig.DEBUG) {
        Log.e(tagText, logText)
    }
}