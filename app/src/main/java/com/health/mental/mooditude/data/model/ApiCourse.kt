package com.health.mental.mooditude.data.model

import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
data class ApiCourse(
    var courseId: String = "",
    var title: String = "",
    var description: String = "",
    var heroImage: String = "",
    var heroImageBrightness: Int = 0,
    var category: String = "",
    var order: Int = 100,
    var expert: Expert? = null,
    var isActive: Boolean = true,
    var lessons: HashMap<String, Lesson> = HashMap<String, Lesson>(),

    var appStoreId: String? = null,
    var isPremium: Boolean = false,
    var isFeatured: Boolean = false,

    var version: Int = 1,
    var publishedDate: Date? =  null,
    var availabilityDate: Date? = null,
    var voteInFavor: Int = 0,
    var voteAgainst: Int = 0,


    // Statistics
    //var stat = CourseStatistics()

    //var progress: CourseProgress? = nil
)
{
}
