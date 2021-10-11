package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.PostCategory

/**
* The Data Access Object for the [PostCategory] class.
*/
@Dao
interface PostCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<PostCategory>)

    @Update
    fun update(data: PostCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: PostCategory)

    @Query("delete from ForumCategory where categoryId = :id")
    fun deleteById(id: Int)

    @Query("select * from ForumCategory where categoryId = :id")
    fun getById(id: Int): PostCategory

    // Method #4
    @Delete
    fun delete(article: PostCategory)

    // Method #5
    @Query("select * from ForumCategory  where isActive = 1")
    fun getAll(): List<PostCategory>

    @Query("DELETE FROM ForumCategory")
    fun deleteAll()

}
