package com.health.mental.mooditude.data.api

import com.health.mental.mooditude.data.model.ApiArticle


interface ApiHelper {

    suspend fun getArticles(): List<ApiArticle>
}