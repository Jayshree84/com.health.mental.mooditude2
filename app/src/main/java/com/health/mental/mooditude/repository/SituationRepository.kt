package com.health.mental.mooditude.repository

import androidx.lifecycle.LiveData
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.dao.UserActivityDao
import com.health.mental.mooditude.data.entity.UserActivity
import com.health.mental.mooditude.data.model.ActivityGroup
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.createdNewActivity
import com.health.mental.mooditude.warnLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors


/**
 * Created by Jayshree Rathod on 18,August,2021
 */
class SituationRepository(val mAppDb: AppDatabase) {
    // below line is the create a variable
    // for dao and list for all courses.
    private var dao: UserActivityDao

    private val TAG = this.javaClass.simpleName

    // creating a constructor for our variables
    // and passing the variables to it.
    init {
        dao = mAppDb.userActivityDao()
    }


    fun fetchAll(): LiveData<List<UserActivity>> {
        return dao.getAll()
    }

    fun updateUsageCount(list:ArrayList<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateCount(list)
        }
    }

    fun saveRecord(record:UserActivity) {
        CoroutineScope(Dispatchers.IO).launch {
            if (record.activityId.isEmpty()) {
                record.activityId = UUID.randomUUID().toString()
            }
            record.count = 0
            record.group = ActivityGroup.other
            dao.insert(record)
        }
        //log event
        EventCatalog.instance.createdNewActivity(record.title)
    }

    fun fetchRecent(): List<UserActivity> {
        return dao.getFrequentlyUsed()
    }

    fun insertAllActivities(list: List<UserActivity>, removeAll: Boolean = false) {
        val callable = object : Callable<Any> {
            override fun call() {
                if (removeAll) {
                    dao.deleteAll()
                    warnLog(TAG, "ALL DELETED")
                }
                dao.insertAll(list)
                debugLog(TAG, "Table created ")
            }
        }
        Executors.newSingleThreadExecutor().submit(callable)
    }
}