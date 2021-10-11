package com.mindorks.example.coroutines.data.local

import com.health.mental.mooditude.data.entity.Article

interface DatabaseHelper {

    //suspend fun getArticles(): LiveData<List<Article>>
    suspend fun getArticles(): List<Article>

    suspend fun insertArticles(articles: List<Article>)

    suspend fun saveArticle(article: Article)

}