package com.health.mental.mooditude.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.health.mental.mooditude.data.entity.Entry
import java.util.*
import kotlin.collections.ArrayList

/**
* The Data Access Object for the [Entry] class.
*/
@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Entry>)

    @Update
    fun update(data: Entry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data:Entry)

    @Query("delete from Entry where entryId = :id")
    fun deleteById(id: String)

    @Query("select * from Entry where entryId = :id")
    fun getById(id: String): Entry

    // Method #4
    @Delete
    fun delete(entry: Entry)

    // Method #5
    @Query("select * from Entry where deleted = 0 ORDER BY postedDate DESC")
    fun getAll(): LiveData<List<Entry>>

    @Query("update Entry SET synced = 1 where entryId = :entryId")
    fun markSynced(entryId: String)

    @Query("update Entry SET deleted = 1 where entryId = :entryId")
    fun markDeleted(entryId: String)

    @Query("select * from Entry where postedDate >= :startDate AND postedDate <= :endDate AND entryType = 'mood' AND deleted = 0 ORDER BY postedDate DESC")
    fun fetchMultipleMoodEntries(startDate: Date, endDate: Date): List<Entry>

    @Query("select * from Entry where postedDate >= :startDate AND postedDate <= :endDate AND deleted = 0 ORDER BY postedDate DESC")
    fun fetchMultipleEntries(startDate: Date, endDate: Date): List<Entry>

    @Query("select * from Entry where postedDate < :startDate AND deleted = 0 ORDER BY postedDate DESC LIMIT :pageSize")
    fun fetchMultipleEntries(startDate: Date, pageSize:Int): List<Entry>

    @Query("select * from Entry where deleted = 0 ORDER BY postedDate DESC LIMIT :pageSize")
    fun fetchMultipleEntries(pageSize:Int): List<Entry>

    @Query("delete from Entry where entryId IN (:list)")
    fun deleteAll(list: ArrayList<String>)
}
