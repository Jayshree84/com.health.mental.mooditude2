package com.health.mental.mooditude.services.instrumentation

import com.health.mental.mooditude.data.entity.M3Assessment

/**
 * Created by Jayshree Rathod on 01,October,2021
 */
fun EventCatalog.openedAssessment(compltion: Double) {
    val map = mapOf<String, Any>(
        Pair("completion", (compltion * 100).toInt())
    )
    event("openedAssessment", map, true)
}

fun EventCatalog.tookAssessment(assessment: M3Assessment, location: String) {
    val map = mapOf<String, Any>(
        Pair("allScore", assessment.allScore),
        Pair("depressionScore", assessment.depressionScore),
        Pair("anxietyScore", assessment.getAnxietyScore()),
        Pair("ptsdScore", assessment.ptsdScore),
        Pair("bipolarScore", assessment.bipolarScore),
        Pair("gatewayScore", assessment.gatewayScore),
        Pair("location", location)
    )

    val review = assessment.allScore < 32
    event("tookAssessment", map, review)
}

fun EventCatalog.viewedAssessmentChart(condition: String) {
    val map = mapOf<String, Any>(
        Pair("mentalHealthCondition", condition)
    )
    event("viewedAssessmentChart", map, true)
}

fun EventCatalog.viewedPastAssessments() {
    event("viewedAllAssessments", mapOf(), true)
}

fun EventCatalog.viewedPastAssment(assessment: M3Assessment) {
    val map = mapOf<String, Any>(
        Pair("assessmentId", assessment.id)
    )
    event("viewedAssessment", map, true)
}

fun EventCatalog.learnMoreAboutM3() {
    event("learnMoreAboutM3ButtonTapped", mapOf())
}