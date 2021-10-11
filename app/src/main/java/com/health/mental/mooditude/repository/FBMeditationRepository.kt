package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.entity.Article
import com.health.mental.mooditude.data.entity.MeditationCategory
import com.health.mental.mooditude.data.entity.MeditationInfo
import com.health.mental.mooditude.data.model.ApiMeditationCategory
import com.health.mental.mooditude.data.model.ApiMeditationInfo
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
class FBMeditationRepository(
    private val mAppDb: AppDatabase,
    private val rdb: DatabaseReference,
    private val mFireStore: FirebaseFirestore
) {

    private val TAG = this.javaClass.simpleName
    private val MEDITATION_CATEGORY_PATH = "meditationCategories/%s/"
    private val MEDITATION_INFO_PATH = "meditationInfo/%s/"

    private val catDao = mAppDb.meditationCategoryDao()
    private val meditationDao = mAppDb.meditationDao()

    fun fetchMeditationCategories(language: String) {
        val path = String.format(MEDITATION_CATEGORY_PATH, language)
        val query1 = rdb.child(path)
        query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val list2 = ArrayList<MeditationCategory>()
                    val catList = ArrayList<ApiMeditationCategory>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val meditationCategory =
                            Gson().fromJson(json, ApiMeditationCategory::class.java)
                        meditationCategory.categoryId = key as String
                        catList.add(meditationCategory)


                        //Now save this data to db
                        val meditationCatRecord = MeditationCategory.readFromApi(meditationCategory)
                        list2.add(meditationCatRecord)
                    }
                    //now call listener to save to database
                    insertAllCategories(list2, true)
                }
            }
        })
    }


    fun fetchMeditations(language: String) {
        val path = String.format(MEDITATION_INFO_PATH, language)
        val query1 = rdb.child(path)
        query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val infoList = ArrayList<ApiMeditationInfo>()
                    val list2 = ArrayList<MeditationInfo>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val meditationInfo =
                            Gson().fromJson(json, ApiMeditationInfo::class.java)
                        meditationInfo.meditationId = key as String
                        infoList.add(meditationInfo)

                        //Now save this data to db
                        val meditationInfoRecord = MeditationInfo.readFromApi(meditationInfo)
                        list2.add(meditationInfoRecord)
                    }

                    //call listener to dave on local database
                    insertAllMeditations(list2, true)
                }
            }
        })
    }

    fun uploadMeditations(meditations: ArrayList<ApiMeditationInfo>, language: String) {
        val path = String.format(MEDITATION_INFO_PATH, language)
        meditations.forEach {
            rdb.child(path).child(it.meditationId).setValue(it)
        }
    }

    fun uploadMeditationCategories(
        meditationCats: ArrayList<MeditationCategory>,
        language: String
    ) {
        val path = String.format(MEDITATION_CATEGORY_PATH, language)
        meditationCats.forEach {
            rdb.child(path).child(it.categoryId).setValue(it)
        }
    }

    fun insertAllCategories(list: List<MeditationCategory>, removeAll: Boolean = false) {
        val callable = object : Callable<Any> {
            override fun call() {
                if (removeAll) {
                    catDao.deleteAll()
                    warnLog(TAG, "ALL DELETED")
                }
                catDao.insertAll(list)
                debugLog(TAG, "Table created ")
            }
        }
        Executors.newSingleThreadExecutor().submit(callable)
    }

    fun insertAllMeditations(list: List<MeditationInfo>, removeAll: Boolean = false) {
        val callable = object : Callable<Any> {
            override fun call() {
                if (removeAll) {
                    meditationDao.deleteAll()
                    warnLog(TAG, "ALL DELETED")
                }
                meditationDao.insertAll(list)
                debugLog(TAG, "Table created ")
            }
        }
        Executors.newSingleThreadExecutor().submit(callable)
    }

}