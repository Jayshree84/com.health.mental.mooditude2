package com.health.mental.mooditude.data.api

import com.health.mental.mooditude.data.model.ApiArticle


interface ApiService {


    suspend fun getUsers(): List<ApiArticle>

}