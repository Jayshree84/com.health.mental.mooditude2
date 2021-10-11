package com.health.mental.mooditude.services.instrumentation

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.health.mental.mooditude.BuildConfig
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.services.freshchat.ChatService
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject
import java.util.*


/**
 * Created by Jayshree Rathod on 01,October,2021
 */
class EventCatalog private constructor(private val mContext: Application) {

    //used for logging purpose
    protected val TAG = this.javaClass.simpleName
    private val MIXPANEL_TOKEN_DEBUG = "6285a21a5f3a5cc9e24e82c21b19048a"
    private val MIXPANEL_TOKEN = "fa8bf369b0e38339fe7dae860eb9f36a"

    //To enable/disable analytics
    internal var isAnalyticActive: Boolean = true

    internal val mMixPanelApi: MixpanelAPI
    internal val analytics: FirebaseAnalytics
    internal var mIsIdentified = false

    companion object {

        lateinit var instance: EventCatalog

        fun createService(context: Application) {
            instance = EventCatalog(context)
            //instance.initialize()
            instance.activateOrDeactivateAnalytic(true)
        }
    }

    init {
        /*#if DEBUG
        Mixpanel.initialize(token: "6285a21a5f3a5cc9e24e82c21b19048a")
        #else
        Mixpanel.initialize(token: "fa8bf369b0e38339fe7dae860eb9f36a")
        #endif*/
        var token = MIXPANEL_TOKEN
        if (BuildConfig.DEBUG) {
            token = MIXPANEL_TOKEN_DEBUG
        }
        mMixPanelApi = MixpanelAPI.getInstance(mContext, token)
        analytics = Firebase.analytics
    }

    enum class Shareable {
        chart, reward, journalPost, goalDetail, forumPost, assessment
    }

    enum class Screen {
        onboarding, settings, goalDetail, routineDetail, goalValueInput, reminderManager
    }

    /*private fun initialize(user: AppUser) {
        mMixPanelApi.flush()
        setUserProperties(user)
    }*/

    fun activateOrDeactivateAnalytic(activate: Boolean) {
        isAnalyticActive = activate
        //UserDefaults.standard.set(activate ? 0 : 1 , forKey: "isAnalyticActive")
        event("AllowedDataCollection", mapOf(Pair("enabled", activate)), false)

        analytics.setAnalyticsCollectionEnabled(activate)
        if (activate) {
            mMixPanelApi.optInTracking()
        } else {
            mMixPanelApi.optOutTracking()
        }
    }

    fun event(name: String, properties: Map<String, Any>, requestReview: Boolean = false) {

        if (isAnalyticActive) {

            debugLog(TAG, "Properties : " + properties.size)
            val bundle = Bundle()
            for (key in properties.keys) {
                val keyStr = key.replace("\$", "")
                bundle.putString(keyStr, properties.get(key).toString())
            }
            //firebase
            analytics.logEvent(name, bundle)

            //MixPanel
            val jsonObj = JSONObject(properties)
            debugLog(TAG, "Properties : " + jsonObj.toString())
            for (key in jsonObj.keys()) {
                debugLog(TAG, "Properties : Key :  " + key + " : " + jsonObj.get(key))
            }
            mMixPanelApi.track(name, jsonObj)

            ChatService.instance.trackEvent(name, properties)
        }

        /*if (requestReview) {
            AppReview.requestReview()
        }*/
    }

    // MARK: Events

    // Called the first time app is installed and log Search term used by the user to download the app
    fun searchTermUsed(term: String) {
        val map = mapOf<String, Any>(Pair("searchTerm", term.lowercase()))
        mMixPanelApi.registerSuperPropertiesOnce(JSONObject(map))
        event("searchTermUsed", map)
    }


    fun installedApp() {
        val map = mapOf<String, Any>(Pair("plan", "free"), Pair("joinDate", Date()))
        mMixPanelApi.registerSuperPropertiesOnce(JSONObject(map))
        event("installedApp", map)
    }

    fun viewedScreen(screenName: String) {
        val map = mapOf<String, Any>(Pair("screenName", screenName))
        event("viewedScreen", map)
    }

    fun sharedEvent(shareable: Shareable, id: String) {
        val map =
            mapOf<String, Any>(Pair("sharedItemType", shareable.name), Pair("sharedItemId", id))
        event("shared", map)
    }

    fun chatStarted() {
        event("chatStarted", mapOf())
    }

    fun sessionStarted() {
        event("sessionStarted", mapOf())
        mMixPanelApi.unregisterSuperProperty("mood")
    }

    fun badgeCreatedEvent(activity: String, badge: Int) {
        val map = mapOf<String, Any>(Pair("activity", activity), Pair("badge", badge))
        event("badgeCreated", map)
        mMixPanelApi.people.increment("badges", 1.0)
    }

    fun openedAppFromNotification(category: String, isRemote: Boolean) {
        val map = mapOf<String, Any>(Pair("category", category), Pair("isRemote", isRemote))
        event("openedAppFromNotification", map)
    }

    fun changedPasscodeBioMetricSettingEvent(enabled: Boolean) {
        val map = mapOf<String, Any>(Pair("activated", enabled))
        event("changedPasscodeBioMetric", map, true)
    }

}