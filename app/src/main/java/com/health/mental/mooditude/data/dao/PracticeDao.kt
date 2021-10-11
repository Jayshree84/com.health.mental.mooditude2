package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.Practice

/**
* The Data Access Object for the [Practice] class.
*/
@Dao
interface PracticeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Practice>)

    @Update
    fun update(data: Practice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Practice)

    @Query("delete from Practice where practiceId = :id")
    fun deleteById(id: Int)

    @Query("select * from Practice where practiceId = :id")
    fun getById(id: Int): Practice

    // Method #4
    @Delete
    fun delete(article: Practice)

    // Method #5
    @Query("select * from Practice")
    fun getAll(): List<Practice>

}
