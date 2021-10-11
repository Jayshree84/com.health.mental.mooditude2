package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.PromptCategory

/**
* The Data Access Object for the [PromptCategory] class.
*/
@Dao
interface JournalPromptCatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<PromptCategory>)

    @Update
    fun update(data: PromptCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: PromptCategory)

    @Query("delete from PromptCategory where categoryId = :id")
    fun deleteById(id: Int)

    @Query("select * from PromptCategory where categoryId = :id")
    fun getById(id: Int):PromptCategory

    // Method #4
    @Delete
    fun delete(article: PromptCategory)

    // Method #5
    @Query("select * from PromptCategory")
    fun getAll(): List<PromptCategory>

    @Query("DELETE FROM PromptCategory")
    fun deleteAll()

}
