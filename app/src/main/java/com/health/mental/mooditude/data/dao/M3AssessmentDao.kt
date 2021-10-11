package com.health.mental.mooditude.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.utils.CalendarUtils
import java.util.*

/**
* The Data Access Object for the [M3Assessment] class.
*/
@Dao
interface M3AssessmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<M3Assessment>)

    @Update
    fun update(data: M3Assessment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: M3Assessment)

    @Query("delete from M3Assessment where id = :id")
    fun deleteById(id: Int)

    @Query("select * from M3Assessment where id = :id")
    fun getById(id: Int): M3Assessment

    @Query("select * from M3Assessment where createDate = :date")
    fun getByToday( date:Date = Date(CalendarUtils.getStartTimeOfDay())): LiveData<M3Assessment>

    @Query("select * from M3Assessment ORDER BY createDate DESC LIMIT 1")
    fun getByLatest(): LiveData<M3Assessment>

    @Query("select * from M3Assessment ORDER BY createDate DESC LIMIT 1")
    fun getByLatestBlocking(): M3Assessment

    @Query("select * from M3Assessment where createDate > :date")
    fun getCurrentMonthList(date:Date = Date(CalendarUtils.getPreviousMonthTime(System.currentTimeMillis()))): LiveData<List<M3Assessment>>

    @Query("select * from M3Assessment where createDate > :date")
    fun getQuarterList(date:Date = Date(CalendarUtils.getStartOfMonth(System.currentTimeMillis(), 3))): LiveData<List<M3Assessment>>

    // Method #4
    @Delete
    fun delete(article: M3Assessment)

    // Method #5
    @Query("select * from M3Assessment ORDER BY createDate DESC")
    fun getAll(): LiveData<List<M3Assessment>>

    @Query("delete from M3Assessment where id IN (:list)")
    fun deleteAll(list: ArrayList<String>)

}
