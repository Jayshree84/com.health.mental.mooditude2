package com.health.mental.mooditude.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.health.mental.mooditude.data.entity.UserActivity

/**
* The Data Access Object for the [UserActivity] class.
*/
@Dao
interface UserActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<UserActivity>)

    @Update
    fun update(data: UserActivity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data:UserActivity)

    @Query("delete from UserActivity where activityId = :id")
    fun deleteById(id: String)

    @Query("select * from UserActivity where activityId = :id")
    fun getById(id: String): UserActivity

    // Method #4
    @Delete
    fun delete(activity: UserActivity)

    // Method #5
    @Query("select * from UserActivity where deleted = 0")
    fun getAll(): LiveData<List<UserActivity>>

    // Method #5
    @Query("select * from UserActivity where count > 0 ORDER BY count DESC")
    //fun getAll(): LiveData<List<UserActivity>>
    fun getFrequentlyUsed(): List<UserActivity>

    @Query("update UserActivity SET count = count+1 where activityId IN (:list)")
    fun updateCount(list:ArrayList<String>)

    @Query("DELETE FROM UserActivity")
    fun deleteAll()
}
