package com.mindorks.example.coroutines.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.Article
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.*
import com.health.mental.mooditude.worker.SeedDatabaseWorker
import com.health.mental.mooditude.worker.SeedDatabaseWorker.Companion.KEY_DATA_FILENAME

object DatabaseBuilder {

    private val TAG = this.javaClass.simpleName
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    fun clearDatabase() {
        INSTANCE = null
    }

    private fun insertUserActivity(context: Context) {
        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
            .setInputData(workDataOf(KEY_DATA_FILENAME to USER_ACTIVITY_FILENAME))
            .build()
        val workMgr = WorkManager.getInstance(context)
        workMgr.enqueue(request)
    }

    private fun insertArticles(context: Context) {
        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
            .setInputData(workDataOf(KEY_DATA_FILENAME to ARTICLE_DATA_FILENAME))
            .build()
        val workMgr = WorkManager.getInstance(context)
        workMgr.enqueue(request)
    }

    private fun insertMeditationCategory(context: Context) {
        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
            .setInputData(workDataOf(KEY_DATA_FILENAME to MEDITATION_CATEGORY_FILENAME))
            .build()
        val workMgr = WorkManager.getInstance(context)
        workMgr.enqueue(request)
    }

    private fun insertPromptCategory(context: Context) {
        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
            .setInputData(workDataOf(KEY_DATA_FILENAME to PROMPT_CATEGORY_FILENAME))
            .build()
        val workMgr = WorkManager.getInstance(context)
        workMgr.enqueue(request)
    }

    private fun insertJournalPrompt(context: Context) {
        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
            .setInputData(workDataOf(KEY_DATA_FILENAME to JOURNAL_PROMPT_FILENAME))
            .build()
        val workMgr = WorkManager.getInstance(context)
        workMgr.enqueue(request)
    }

    private fun buildRoomDB(context: Context) =

        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        debugLog(TAG, "Database created")

                        //Insert data from assets
                        DBManager.instance.insertDefaultData()
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        debugLog(TAG, "Database onDestructiveMigration")
                        super.onDestructiveMigration(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        debugLog(TAG, "Database onOpen" + db)
                        super.onOpen(db)


                    }
                }
            )
            .build();


    //to update record
    fun updateArticle(context: Context, article: Article) {
        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
            .setInputData(workDataOf(KEY_DATA_FILENAME to ARTICLE_DATA_FILENAME))
            .build()
        val workMgr = WorkManager.getInstance(context)
        workMgr.enqueue(request)
    }

}