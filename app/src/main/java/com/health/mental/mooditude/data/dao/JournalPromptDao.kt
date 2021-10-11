package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.JournalPrompt

/**
* The Data Access Object for the [JournalPrompt] class.
*/
@Dao
interface JournalPromptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<JournalPrompt>)

    @Update
    fun update(data: JournalPrompt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: JournalPrompt)

    @Query("delete from JournalPrompt where promptId = :id")
    fun deleteById(id: String)

    @Query("select * from JournalPrompt where promptId = :id")
    fun getById(id: String): JournalPrompt

    // Method #4
    @Delete
    fun delete(article: JournalPrompt)

    // Method #5
    @Query("select * from JournalPrompt")
    fun getAll(): List<JournalPrompt>

    @Query("DELETE FROM JournalPrompt")
    fun deleteAll()
}
