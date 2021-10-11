package com.health.mental.mooditude.data.model

/**
 * Created by Jayshree Rathod on 12,July,2021
 */
data class UserProperty(
    var moodRecords: Int = 0,
    var journalRecords: Int = 0,
    var cbtRecords: Int = 0,
    var goalsCompleted: Int = 0,
    var routineCompleted: Int = 0,
    var meditationMinutes: Int = 0,
    var coursesBrowsed: Int = 1,
    var coursesCompleted: Int = 0,
    var themeId: String = "Light",
    var trackingPeriods: Boolean = false,
    var remindersEnabled: Boolean = false,
    var copingACtivityUsed: Int = 0,
    var shared: Int = 0,
    var source: String? = null,
    var topGoal: String? = null,
    var topChallenes: String? = null,
    var keywordUsed: String? = null,

    var nps: Int = 0,
    var paywallViewed: Int = 0,
    var iPad: Boolean = false
)
{}
