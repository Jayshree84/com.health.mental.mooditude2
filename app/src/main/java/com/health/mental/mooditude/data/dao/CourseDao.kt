package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.Course

/**
* The Data Access Object for the [Course] class.
*/
@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Course>)

    @Update
    fun update(data: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Course)

    @Query("delete from Course where courseId = :id")
    fun deleteById(id: Int)

    @Query("select * from Course where courseId = :id")
    fun getById(id: Int): Course

    // Method #4
    @Delete
    fun delete(article: Course)

    // Method #5
    @Query("select * from Course")
    fun getAll(): List<Course>

}
