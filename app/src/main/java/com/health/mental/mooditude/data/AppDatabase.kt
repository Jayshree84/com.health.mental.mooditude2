package com.health.mental.mooditude.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.health.mental.mooditude.data.converters.UserActivityConverters
import com.health.mental.mooditude.data.converters.UserFeelingConverters
import com.health.mental.mooditude.data.dao.*
import com.health.mental.mooditude.data.entity.*
import com.health.mental.mooditude.utils.RoomConverters

/**
 * The Room database for this app
 */
@Database(
    entities = [Article::class,
        Badge::class,
        MeditationInfo::class,
        MeditationCategory::class,
        PostCategory::class,
        JournalPrompt::class,
        PromptCategory::class,
        Reward::class,
        Practice::class,
        Course::class,
        ServiceableState::class,
        M3Assessment::class,
        TherapistRequest::class,
        UserActivity::class,
        Entry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class, UserActivityConverters::class, UserFeelingConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun meditationDao(): MeditationDao
    abstract fun meditationCategoryDao(): MeditationCategoryDao
    abstract fun rewardDao(): RewardDao
    abstract fun postCategoryDao(): PostCategoryDao
    abstract fun journalPromptDao(): JournalPromptDao
    abstract fun journalPromptCatDao(): JournalPromptCatDao
    abstract fun courseDao(): CourseDao
    abstract fun practiceDao(): PracticeDao
    abstract fun serviceableStateDao(): ServiceableStateDao
    abstract fun articleDao(): ArticleDao

    abstract fun m3assessmentDao(): M3AssessmentDao

    abstract fun therapistRequestDao(): TherapistRequestDao

    abstract fun userActivityDao(): UserActivityDao

    abstract fun entryDao(): EntryDao
}
