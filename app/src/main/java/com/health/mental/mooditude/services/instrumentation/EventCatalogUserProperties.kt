package com.health.mental.mooditude.services.instrumentation

import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.AppUser
import com.health.mental.mooditude.services.freshchat.ChatService
import com.health.mental.mooditude.utils.DATE_FORMAT_JOIN
import com.health.mental.mooditude.utils.DATE_FORMAT_MOOD_TIME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jayshree Rathod on 02,October,2021
 */
fun EventCatalog.setUserProperty(name: String, value: Int, isSuperProperty: Boolean = false) {
    mMixPanelApi.people.set(name, value)

    analytics.setUserProperty(name, value.toString())
    ChatService.instance.setUserProperty(name, value.toString())

    if (isSuperProperty) {
        mMixPanelApi.registerSuperProperties(JSONObject(mapOf(Pair(name, value))))
    }

}

fun EventCatalog.setUserProperty(name: String, value: Boolean, isSuperProperty: Boolean = false) {
    mMixPanelApi.people.set(name, value)

    val strValue = if (value) "true" else "false"
    analytics.setUserProperty(name, strValue)
    ChatService.instance.setUserProperty(name, strValue)

    if (isSuperProperty) {
        mMixPanelApi.registerSuperProperties(JSONObject(mapOf(Pair(name, value))))
    }

}

fun EventCatalog.setUserProperty(name: String, value: ArrayList<String>) {
    mMixPanelApi.people.set(name, value)

    val strValue = value.joinToString(",")
    analytics.setUserProperty(strValue, name)
    ChatService.instance.setUserProperty(name, strValue)
}

fun EventCatalog.setUserProperty(name: String, value: String, isSuperProperty: Boolean = false) {
    mMixPanelApi.people.set(name, value)
    analytics.setUserProperty(name, value)
    ChatService.instance.setUserProperty(name, value)

    if (isSuperProperty) {
        mMixPanelApi.registerSuperProperties(JSONObject(mapOf(Pair(name, value))))
    }
}


fun EventCatalog.setUserProperties(user: AppUser) {
    //if !isAnalyticActive || user.isLocal { return }
    if (!isAnalyticActive) {
        return
    }


    val name = user.name
    val topGoal = if (user.topGoal == null) "-" else user.topGoal!!.name
    val topChallenges = user.topChallenges
    val commited = user.committedToSelfhelp.toString()
    val customerType = "Free"//Store.shared.subscription?.customerType.rawValue ?? "-"
    val paymentType = "Free" //Store.shared.subscription?.paymentType ?? "-"

    val memberSince = SimpleDateFormat(DATE_FORMAT_JOIN, Locale.US).format(user.memberSince)
    val knowCbt = user.knowCbt.toString()
    val goingToTherapy = user.goingToTherapy.toString()
    val ageGroup = user.ageGroup.toString()
    val gender = user.gender.toString()


    // Mixpanel Super Properties
    mMixPanelApi.registerSuperProperties(
        JSONObject(
            mapOf(
                Pair("plan", customerType),
                Pair("joinDate", memberSince),
                Pair("committedToSelfhelp", commited),
                Pair("topGoal", topGoal),
                Pair("knowCbt", knowCbt),
                Pair("goingToTherapy", goingToTherapy),
                Pair("gender", gender),
                Pair("ageGroup", ageGroup),
                Pair("topChallenges", topChallenges)
            )
        )
    )

    // Set People Properties
    mMixPanelApi.people.setOnce(
        JSONObject(
            mapOf(
                Pair("\$email", user.email),
                Pair("joinDate", memberSince)
            )
        )
    )

    mMixPanelApi.people.set(
        JSONObject(
            mapOf(
                Pair("\$name", user.name),
                Pair("plan", customerType),
                Pair("committedToSelfhelp", commited),
                Pair("topGoal", topGoal),
                Pair("knowCbt", knowCbt),
                Pair("goingToTherapy", goingToTherapy),
                Pair("gender", gender),
                Pair("ageGroup", ageGroup),
                Pair("paymentType", paymentType),
                Pair("topChallenges", topChallenges)
            )
        )
    )


    // Analytics
    analytics.setUserProperty("name", name)
    analytics.setUserProperty("topGoal", topGoal)
    analytics.setUserProperty("topChallenges", topChallenges)
    analytics.setUserProperty("customerType", customerType)
    analytics.setUserProperty("joinDate", memberSince)
    analytics.setUserProperty("comittedToSelfHelp", commited)
    analytics.setUserProperty("paymentType", paymentType)
    analytics.setUserProperty("knowCbt", knowCbt)
    analytics.setUserProperty("goingToTherapy", goingToTherapy)
    analytics.setUserProperty("ageGroup", ageGroup)
    analytics.setUserProperty("gender", gender)


    //FreshChat Properties.
    //#if !DEBUG
    ChatService.instance.setUserProperty("joinDate", memberSince)
    ChatService.instance.setUserProperty("customerType", customerType)
    ChatService.instance.setUserProperty("comittedToSelfHelp", commited)
    ChatService.instance.setUserProperty("paymentType", paymentType)
    ChatService.instance.setUserProperty("knowCbt", knowCbt)
    ChatService.instance.setUserProperty("goingToTherapy", goingToTherapy)
    ChatService.instance.setUserProperty("topGoal", topGoal)
    ChatService.instance.setUserProperty("challenges", topChallenges)
    ChatService.instance.setUserProperty("ageGroup", ageGroup)
    ChatService.instance.setUserProperty("gender", gender)
    //#endif


    /*if let expiry = Store.shared.subscription?.expiryDate {
        self.mixpanel.people.append(properties: ["expiryDate": expiry])

        let expiryDate = expiry.toString(dateStyle: .short, timeStyle: .none, relativeDate: false)
        Analytics.setUserProperty(expiryDate, forName: "expiryDate")
        ChatService.shared.setUserProperty("expiryDate", expiryDate)
    }*/

    CoroutineScope(Dispatchers.IO).launch {
        val lastestAssessment = DBManager.instance.getLastestAssessmentBlocking()

        if (lastestAssessment != null) {
            mMixPanelApi.people.append("lastAssessmentDate", lastestAssessment.createDate)
            val strDate = SimpleDateFormat(
                DATE_FORMAT_MOOD_TIME,
                Locale.US
            ).format(lastestAssessment.createDate)

            analytics.setUserProperty("lastAssessmentDate", strDate)
            ChatService.instance.setUserProperty("lastAssessmentDate", strDate)
        }
    }


}