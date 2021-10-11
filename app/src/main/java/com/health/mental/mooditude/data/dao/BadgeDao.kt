package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.Badge

/**
* The Data Access Object for the [Badges] class.
*/
@Dao
interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Badge>)

    @Update
    fun update(data: Badge)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Badge)

    @Query("delete from Badges where day = :id")
    fun deleteById(id: Int)

    @Query("select * from Badges where day = :id")
    fun getById(id: Int) : Badge

    // Method #4
    @Delete
    fun delete(article: Badge)

    // Method #5
    @Query("select * from Badges")
    fun getAll(): List<Badge>

}
