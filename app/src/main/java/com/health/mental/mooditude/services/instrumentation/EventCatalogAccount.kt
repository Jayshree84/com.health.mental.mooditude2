package com.health.mental.mooditude.services.instrumentation

import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.data.model.AppUser
import org.json.JSONObject
import java.util.*

/**
 * Created by Jayshree Rathod on 01,October,2021
 */
fun EventCatalog.onSignUp(user: AppUser) {
    //mixpanel.alias('new_id', 'existing_id');
    mMixPanelApi.alias(user.userId, mMixPanelApi.distinctId)

    mMixPanelApi.identify(user.userId)
    mMixPanelApi.people.identify(user.userId)
    mIsIdentified = true

    // Sets user 13793's "Plan" attribute to "Premium"
    //mMixPanelApi.people.set("Plan", "Premium");

    var isCompleted =
        SharedPreferenceManager.getAssessmentCompleted()
    if (isCompleted == null) {
        isCompleted = false
    }
    val map = mapOf<String, Any>(
        Pair("profileCompleted", user.profileCompleted),
        Pair(
            "atOnboarding",
            isCompleted
        )
    )
    event("accountCreated", map)

    mMixPanelApi.people.setOnce(
        JSONObject(
            mapOf<String, Any>(
                Pair("joinDate", user.memberSince),
                Pair("\$email", user.email)
            )
        )
    )

    mMixPanelApi.people.set(
        JSONObject(
            mapOf<String, Any>(
                Pair("\$name", user.name),
                Pair("\$email", user.email)
            )
        )
    )

    mMixPanelApi.flush()

    analytics.setUserId(user.userId)
}

fun EventCatalog.onLogIn(user: AppUser) {
    if(!mIsIdentified) {
        mMixPanelApi.identify(user.userId)
        mMixPanelApi.people.identify(user.userId)
    }

    analytics.logEvent("login", null)
    mMixPanelApi.track("loggedIn")

    // Sets user 13793's "Plan" attribute to "Premium"
    //mMixPanelApi.people.set("Plan", "Premium");
    setUserProperties(user)
}

fun EventCatalog.onLogout() {
    mMixPanelApi.reset()
}

fun EventCatalog.accountDeleted() {
    event("accountDeleted", mapOf())
    mMixPanelApi.reset()
    analytics.resetAnalyticsData()
}

fun EventCatalog.loggedOut() {
    event("loggedOut", mapOf())
    mMixPanelApi.reset()
    analytics.resetAnalyticsData()
}

fun EventCatalog.resetPassword() {
    event("resetPasswordEmailRequested", mapOf())
}

fun EventCatalog.resetPasswordEmailSent() {
    event("resetPasswordEmailSent", mapOf())
}