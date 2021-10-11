package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.MeditationInfo

/**
* The Data Access Object for the [MeditationInfo] class.
*/
@Dao
interface MeditationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<MeditationInfo>)

    @Update
    fun update(data: MeditationInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: MeditationInfo)

    @Query("delete from Meditation where meditationId = :id")
    fun deleteById(id: Int)

    @Query("select * from Meditation where meditationId = :id")
    fun getById(id: Int): MeditationInfo

    // Method #4
    @Delete
    fun delete(article: MeditationInfo)

    // Method #5
    @Query("select * from Meditation")
    fun getAll(): List<MeditationInfo>

    @Query("DELETE FROM Meditation")
    fun deleteAll()
}
