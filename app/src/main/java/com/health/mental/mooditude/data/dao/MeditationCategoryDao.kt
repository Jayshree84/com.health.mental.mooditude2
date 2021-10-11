package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.MeditationCategory

/**
* The Data Access Object for the [MeditationCategory] class.
*/
@Dao
interface MeditationCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<MeditationCategory>)

    @Update
    fun update(data: MeditationCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: MeditationCategory)

    @Query("delete from MeditationCategory where categoryId = :id")
    fun deleteById(id: Int)

    @Query("select * from MeditationCategory where categoryId = :id")
    fun getById(id: Int): MeditationCategory

    // Method #4
    @Delete
    fun delete(article: MeditationCategory)

    // Method #5
    @Query("select * from MeditationCategory")
    fun getAll(): List<MeditationCategory>

    @Query("DELETE FROM MeditationCategory")
    fun deleteAll()
}
