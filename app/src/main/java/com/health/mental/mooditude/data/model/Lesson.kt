package com.health.mental.mooditude.data.model

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
data class Lesson(
    var courseId: String = "",
    var lessonId: String = "",
    var title: String = "",
    var objectives: String = "",
    var order: Int = 0,
    var isActive: Boolean = true,
    var isPremium: Boolean = false
)
{
    enum class CourseLessonState{
        none,              //User did not purchased course till now.
        completed,         //User completed this lesson.
        locked,           //User did not finished prevous lesson till now.
        current            //Now turn for this lesson.
    }
}
