package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.entity.Practice
import com.health.mental.mooditude.data.model.ApiPractice
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBRoutineRepository(
    private val mAppDb: AppDatabase,
    private val rdb: DatabaseReference,
    private val mFireStore: FirebaseFirestore
) {

    private val TAG = this.javaClass.simpleName
    private val PRACTICES_PATH = "practices/%s"

    fun getAllPractices(language: String) {
        val path = String.format(PRACTICES_PATH, language)
        val query1 = rdb.child(path)
        //query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : Message : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val list = ArrayList<ApiPractice>()
                    val list2 = ArrayList<Practice>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val practice =
                            Gson().fromJson(json, ApiPractice::class.java)
                        //check for id
                        practice.id = key
                        list.add(practice)

                        //Now save this data to db
                        val record = Practice.fromApiData(practice)
                        list2.add(record)
                    }
                    //call listener to save to local database
                    saveToLocal(list2)
                }
            }
        })
    }


    private fun saveToLocal(list: ArrayList<Practice>) {
        CoroutineScope(Dispatchers.IO).launch {
            mAppDb.practiceDao().insertAll(list)
            debugLog(TAG, "onRoutineDataReceived :: INSERTED : " + list.size)
        }
    }
}