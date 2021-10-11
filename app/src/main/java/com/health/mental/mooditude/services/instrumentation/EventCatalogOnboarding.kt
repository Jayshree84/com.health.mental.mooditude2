package com.health.mental.mooditude.services.instrumentation

/**
 * Created by Jayshree Rathod on 01,October,2021
 */
fun EventCatalog.onboardingStep(stepId: String){
    event("onboardingStep_"+stepId, mapOf())
}


fun EventCatalog.onboarding_MoodTracking(){
    event("onboarding_MoodTracking",mapOf())
}


fun EventCatalog.onboarding_IdentifyThinkingError(){
    event("onboarding_IdentifyThinkingTraps", mapOf())
}

fun EventCatalog.onboarding_Journaling(){
    event("onboarding_Journaling", mapOf())
}

fun EventCatalog.onboarding_Meditation(){
    event("onboarding_Meditation", mapOf())
}

fun EventCatalog.onboarding_Routine(){
    event("onboarding_Routine", mapOf())
}

fun EventCatalog.onboarding_CommittedToSelfHelp(commited: Boolean){
    val map = mapOf<String, Any>(
        Pair("committed", commited),
    )
    event("committedToSelfHelp", map)
    setUserProperty("committedToSelfHelp", commited, true)
}


fun EventCatalog.onboarding_checkedGrantOption(){
    val map = mapOf<String, Any>(
        Pair("screen", EventCatalog.Screen.onboarding.name),
    )
    event("checkedMooditudeGrant", map)
}


fun EventCatalog.completedOnboarding(){

    //Pair("purchased", Store.shared.hasSubscription),
    val map = mapOf<String, Any>(
        Pair("purchased", false),
    )
    event("onboardingCompleted", map)
}
    