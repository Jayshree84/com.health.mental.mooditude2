package com.health.mental.mooditude.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.mental.mooditude.data.api.ApiHelper
import com.health.mental.mooditude.data.entity.Article
import com.health.mental.mooditude.utils.Resource
import com.mindorks.example.coroutines.data.local.DatabaseHelper
import kotlinx.coroutines.launch

class RoomDBViewModel(private val apiHelper: ApiHelper, private val dbHelper: DatabaseHelper) :
    ViewModel() {

    private val articles = MutableLiveData<Resource<List<Article>>>()

    init {
        fetchArticles()
        //addArticles()
    }

    private fun fetchArticles() {
        //return articles
        viewModelScope.launch {
            articles.postValue(Resource.loading(null))
            try {
                val articlesFromDb = dbHelper.getArticles()
                if (articlesFromDb.isEmpty()) {
                    val articlesFromApi = apiHelper.getArticles()
                    val articlesToInsertInDB = mutableListOf<Article>()

                    for (apiArticle in articlesFromApi) {
                        val article = Article.readFromApi(apiArticle)
                        if(article != null) {
                            articlesToInsertInDB.add(article)
                        }
                    }

                    dbHelper.insertArticles(articlesToInsertInDB)

                    articles.postValue(Resource.success(articlesToInsertInDB))

                } else {
                    articles.postValue(Resource.success(articlesFromDb))
                }


            } catch (e: Exception) {
                articles.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getArticles(): LiveData<Resource<List<Article>>> {
        return articles
    }

}