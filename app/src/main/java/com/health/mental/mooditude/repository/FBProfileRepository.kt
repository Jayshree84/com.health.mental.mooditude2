package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.entity.ServiceableState
import com.health.mental.mooditude.data.model.ApiServiceableState
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBProfileRepository(
    private val mAppDb: AppDatabase,
    private val rdb: DatabaseReference,
    private val mFireStore: FirebaseFirestore
) {

    private val TAG = this.javaClass.simpleName
    private val STATES_PATH = "states/%s"

    fun getStates(language: String) {
        val path = String.format(STATES_PATH, language)
        val query1 = rdb.child(path)
        query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "Error found : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val keyList = p0.value as ArrayList<String>
                    val list = ArrayList<ApiServiceableState>()
                    val list2 = ArrayList<ServiceableState>()
                    for (key in keyList) {
                        val state = ApiServiceableState(key)
                        list.add(state)

                        //Now save this data to db
                        val record = ServiceableState.fromApiData(state)
                        list2.add(record)
                    }

                    //call listener
                    saveToLocal(list2)
                }
            }
        })
    }

    private fun saveToLocal(list: ArrayList<ServiceableState>) {
        CoroutineScope(Dispatchers.IO).launch {
            mAppDb.serviceableStateDao().insertAll(list)
            debugLog(TAG, "ProfileStates Data Inserted :: " + list.size)
        }
    }
}