package com.health.mental.mooditude.data.dao

import androidx.room.*
import com.health.mental.mooditude.data.entity.Article

/**
* The Data Access Object for the [Article] class.
*/
@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Article>)

    @Update
    fun update(data: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data:Article)

    @Query("delete from Articles where articleId = :id")
    fun deleteById(id: Int)

    @Query("select * from Articles where articleId = :id")
    fun getById(id: Int): Article

    // Method #4
    @Delete
    fun delete(article: Article)

    // Method #5
    @Query("select * from Articles")
    //fun getAll(): LiveData<List<Article>>
    fun getAll(): List<Article>

    @Query("DELETE FROM Articles")
    fun deleteAll()

}
