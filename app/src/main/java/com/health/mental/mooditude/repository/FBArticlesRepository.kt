package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.entity.Article
import com.health.mental.mooditude.data.entity.PromptCategory
import com.health.mental.mooditude.data.model.ApiArticle
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.warnLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBArticlesRepository(
    private val mAppDb: AppDatabase,
    private val rdb: DatabaseReference,
    private val mFireStore: FirebaseFirestore
) {
    private val TAG = this.javaClass.simpleName
    private val ARTICLES_PATH = "articles/%s"

    private val dao = mAppDb.articleDao()

    fun getArticles(language: String) {
        val path = String.format(ARTICLES_PATH, language)
        val query1 = rdb.child(path)
        //query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "Error in Database : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                debugLog(TAG, "p0 : " + p0.key + " : " + p0.exists())
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val list = ArrayList<ApiArticle>()
                    val list2 = ArrayList<Article>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val article = Gson().fromJson(json, ApiArticle::class.java)
                        //check for id
                        article.articleId = key
                        list.add(article)

                        //Now save this data to db
                        val record = Article.readFromApi(article)
                        if (record != null) {
                            list2.add(record)
                        }
                    }

                    //save into local database
                    insertAll(list2, true)
                }
            }
        })
    }


    fun insertAll(list: List<Article>, removeAll:Boolean = false) {
        val callable = object : Callable<Any> {
            override fun call() {
                if(removeAll) {
                    dao.deleteAll()
                    warnLog(TAG, "ALL DELETED")
                }
                dao.insertAll(list)
                debugLog(TAG, "Table created : Article ")
            }
        }
        Executors.newSingleThreadExecutor().submit(callable)
    }
}