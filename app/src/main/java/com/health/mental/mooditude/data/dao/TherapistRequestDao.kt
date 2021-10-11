package com.health.mental.mooditude.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.health.mental.mooditude.data.entity.TherapistRequest

/**
* The Data Access Object for the [TherapistRequest] class.
*/
@Dao
interface TherapistRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<TherapistRequest>)

    @Update
    fun update(data: TherapistRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: TherapistRequest)

    @Query("delete from TherapistRequest where requestId = :id")
    fun deleteById(id: Int)

    @Query("select * from TherapistRequest where requestId = :id")
    fun getById(id: Int): TherapistRequest

    // Method #4
    @Delete
    fun delete(data: TherapistRequest)

    // Method #5
    @Query("select * from TherapistRequest")
    fun getAllRequests(): LiveData<List<TherapistRequest>>

    @Query("select * from TherapistRequest ORDER BY postedDate")
    fun getByLatest(): LiveData<List<TherapistRequest>>

}
