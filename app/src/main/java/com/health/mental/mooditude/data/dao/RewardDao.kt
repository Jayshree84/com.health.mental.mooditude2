package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.Reward

/**
* The Data Access Object for the [Reward] class.
*/
@Dao
interface RewardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Reward>)

    @Update
    fun update(data: Reward)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Reward)

    @Query("delete from Reward where rewardId = :id")
    fun deleteById(id: Int)

    @Query("select * from Reward where rewardId = :id")
    fun getById(id: Int): Reward

    // Method #4
    @Delete
    fun delete(article: Reward)

    // Method #5
    @Query("select * from Reward")
    fun getAll(): List<Reward>

}
