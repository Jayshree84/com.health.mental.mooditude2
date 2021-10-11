package com.health.mental.mooditude.data.api

import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.ApiArticle

class ApiHelperImpl(private val apiService: DBManager) : ApiHelper {

    override suspend fun getArticles(): List<ApiArticle> = apiService.getArticles()
}