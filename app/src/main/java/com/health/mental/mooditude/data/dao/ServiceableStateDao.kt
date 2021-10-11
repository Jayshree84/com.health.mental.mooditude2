package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.ServiceableState

/**
* The Data Access Object for the [ServiceableState] class.
*/
@Dao
interface ServiceableStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<ServiceableState>)

    @Update
    fun update(data: ServiceableState)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: ServiceableState)

    @Query("delete from ServiceableStates where stateId = :id")
    fun deleteById(id: Int)

    @Query("select * from ServiceableStates where stateId = :id")
    fun getById(id: Int): ServiceableState

    // Method #4
    @Delete
    fun delete(article: ServiceableState)

    // Method #5
    @Query("select * from ServiceableStates")
    fun getAll(): List<ServiceableState>

}
