package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.entity.Course
import com.health.mental.mooditude.data.model.ApiCourse
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBCourseRepository(private val mAppDb: AppDatabase,
                         private val rdb: DatabaseReference,
                         private val mFireStore: FirebaseFirestore
) {

    private val TAG = this.javaClass.simpleName
    private val COURSES_PATH = "courses/%s"

    fun getCourses(language: String) {
        val path = String.format(COURSES_PATH, language)
        val query1 = rdb.child(path)
        //query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    debugLog(TAG, "TOTAL COURSES: " + map.size)
                    val list = ArrayList<ApiCourse>()
                    val list2 = ArrayList<Course>()
                    for (key in map.keys) {

                        val json = Gson().toJson(map.get(key))
                        val course =
                            Gson().fromJson(json, ApiCourse::class.java)

                        //check for id
                        course.courseId = key
                        list.add(course)

                        //Now save this data to db
                        val record = Course.fromApiData(course)
                        list2.add(record)
                    }

                    //Call listener
                    saveToLocal(list2)
                }
            }
        })
    }

    private fun saveToLocal(list: ArrayList<Course>) {
        CoroutineScope(Dispatchers.IO).launch {
            mAppDb.courseDao().insertAll(list)
            debugLog(TAG, "onCourseDataReceived :: INSERTED : " + list.size)
        }
    }
}