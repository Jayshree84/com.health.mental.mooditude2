package com.health.mental.mooditude.repository

import androidx.lifecycle.LiveData
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.debugLog
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future


/**
 * Created by Jayshree Rathod on 18,August,2021
 */
class EntryRepository(val mAppDb: AppDatabase) {

    private val TAG = javaClass.simpleName
    private val PAGE_SIZE = 0

    // below line is the create a variable
    // for dao and list for all courses.
    private val dao = mAppDb.entryDao()


    fun fetchAll(): LiveData<List<Entry>> {
        return dao.getAll()
    }

    fun saveRecord(record: Entry) {
        if (record.entryId.isEmpty()) {
            record.entryId = UUID.randomUUID().toString().replace("-", "").uppercase()
            debugLog(TAG, "ID is : " + record.entryId)
        }

        val callable = object : Callable<Any> {
            override fun call() {
                dao.insert(record)
            }
        }

        val future: Future<Any> = Executors.newSingleThreadExecutor().submit(callable);
        future.get();
    }

    fun markSynced(entryId: String?) {
        if (entryId != null) {
            val callable = object : Callable<Any> {
                override fun call() {
                    dao.markSynced(entryId)
                }
            }

            val future: Future<Any> = Executors.newSingleThreadExecutor().submit(callable);
            future.get();
        }
    }

    fun markDeleted(entryId: String?) {
        if (entryId != null) {
            val callable = object : Callable<Any> {
                override fun call() {
                    dao.markSynced(entryId)
                }
            }

            val future: Future<Any> = Executors.newSingleThreadExecutor().submit(callable);
            future.get();
        }
    }

    /*fun fetchMultipleMoodEntries(startDate: Long, endDate: Long): List<Entry> {
        return dao.fetchMultipleMoodEntries(Date(startDate), Date(endDate))
    }*/

    fun saveEntriesToLocal(list: ArrayList<Entry>) {
        val callable = object : Callable<Any> {
            override fun call() {
                dao.insertAll(list)
            }
        }

        val future: Future<Any> = Executors.newSingleThreadExecutor().submit(callable);
        future.get();
    }

    fun deleteEntriesFromLocal(list: ArrayList<String>) {
        val callable = object : Callable<Any> {
            override fun call() {
                dao.deleteAll(list)
            }
        }

        val future: Future<Any> = Executors.newSingleThreadExecutor().submit(callable);
        future.get();
    }

    fun getAllEntries(startDate: Long, endDate: Long) : List<Entry>{
        val callable = object : Callable<List<Entry>> {
            override fun call(): List<Entry> {
                val list = dao.fetchMultipleEntries(Date(startDate), Date(endDate))
                return list
            }
        }

        return Executors.newSingleThreadExecutor().submit(callable).get();
    }


    fun loadMultiple(startDate: Date?, pageSize: Int = PAGE_SIZE) : List<Entry>{
        val callable = object : Callable<List<Entry>> {
            override fun call(): List<Entry> {
                if (startDate != null) {
                    val list = dao.fetchMultipleEntries(startDate, pageSize)
                    return list
                } else {
                    val list = dao.fetchMultipleEntries(pageSize)
                    return list
                }
            }
        }

        return Executors.newSingleThreadExecutor().submit(callable).get();
    }


}