package com.health.mental.mooditude.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.dao.M3AssessmentDao
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.data.model.ApiEntry
import com.health.mental.mooditude.data.model.M3QuestionData
import com.health.mental.mooditude.data.model.M3ScoreMessageData
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.utils.CalendarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class M3AssessmentRepository(private val mAppDb: AppDatabase,
                             private val rdb: DatabaseReference,
                             private val mFireStore: FirebaseFirestore
) {
    private val assessmentDao:M3AssessmentDao

    init {
        assessmentDao = mAppDb.m3assessmentDao()
    }

    private val TAG = this.javaClass.simpleName
    private val M3ASSESMENT_PATH = "m3Assessment/%s"

    private val M3ASSEMENT_COLLECTION = "M3Assessment"
    private val SCORE_COLLECTION = "scores"

    private var listener: ListenerRegistration? = null
    fun removeListener() {
        listener?.remove()
        listener = null
    }

    fun addListener() {
        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            return
        }

        //Fetch 3 months data
        val currTime = System.currentTimeMillis()
        
        val startDate = CalendarUtils.getStartOfMonth(currTime, 3)
        val endDate = CalendarUtils.getEndOfMonth(currTime)
        debugLog(TAG, "START : " + SimpleDateFormat("dd MM yyyy", Locale.US).format(startDate) + "\n" +
                "END : " + SimpleDateFormat("dd MM yyyy", Locale.US).format(endDate))

        val docRef = mFireStore.collection(M3ASSEMENT_COLLECTION)
            .document(user.userId).collection(SCORE_COLLECTION)
            .whereGreaterThanOrEqualTo("createDate", startDate)
            .whereLessThanOrEqualTo("createDate", endDate)

        listener = docRef.addSnapshotListener { value, error ->
            processData(value, error)
        }

    }

    private fun processData(
        snapshot: QuerySnapshot?,
        error: FirebaseFirestoreException?
    ) {
        if (error != null) {
            errorLog(TAG, "Error : " + error.localizedMessage)
            return
        }

        if (snapshot == null) {
            errorLog(TAG, "snapshot NULL")
            return
        }

        debugLog(TAG, "snapshot.metadata.hasPendingWrites() : " + snapshot.metadata.hasPendingWrites())
        debugLog(TAG, "snapshot.metadata.isFromCache : " + snapshot.metadata.isFromCache)
        if (snapshot.metadata.hasPendingWrites() || snapshot.metadata.isFromCache) {
            return
        }
        val listRecordsToAdd = ArrayList<M3Assessment>()
        val listEntriesToDelete = ArrayList<String>()
        val changes = snapshot.documentChanges
        debugLog(TAG, "TOTAL SIZE :: " + changes.size)
        for (change in changes) {
            if (!change.document.exists() || change.document.metadata.hasPendingWrites()) {
                continue
            }

            val doc = change.document
            debugLog(TAG, "Change Type : " + change.type)
            if(change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
                val record = doc.toObject(M3Assessment::class.java)
                record.synced = true
                listRecordsToAdd.add(record)
                debugLog(TAG, "Entry aded : " + record.toString())
            }
            else if(change.type == DocumentChange.Type.REMOVED) {
                listEntriesToDelete.add(doc.id)
                debugLog(TAG, "Entry removed : " + doc.id)
            }
        }

        debugLog(TAG, "Total records to add : " + listRecordsToAdd.size)
        debugLog(TAG, "Total records to delete : " + listEntriesToDelete.size)

        //Add this record to database
        if(listRecordsToAdd.size > 0) {
            saveDataToLocal(listRecordsToAdd)
        }

        if(listEntriesToDelete.size > 0) {
            deleteEntriesFromLocal(listEntriesToDelete)
        }
    }

    private fun saveDataToLocal(list: ArrayList<M3Assessment>) {
        CoroutineScope(Dispatchers.IO).launch {
            assessmentDao.insertAll(list)
            debugLog(TAG, "onM3AssessmentDataReceived :: INSERTED : " + list.size)
        }
    }

    private fun deleteEntriesFromLocal(list: java.util.ArrayList<String>) {
        val callable = object : Callable<Any> {
            override fun call() {
                mAppDb.m3assessmentDao().deleteAll(list)
            }
        }

        val future: Future<Any> = Executors.newSingleThreadExecutor().submit(callable);
        future.get();
    }

    fun getM3AssessmentData(language: String) {
        val path = String.format(M3ASSESMENT_PATH, language)
        val query1 = rdb.child(path)
        //query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>

                    if (map.get("m3Questions") != null) {
                        val questionData = map.get("m3Questions") as ArrayList<HashMap<String, *>>
                        M3QuestionData.processData(questionData)
                    }
                    if (map.get("m3ScoreMessages") != null) {
                        val messagesData =
                            map.get("m3ScoreMessages") as ArrayList<HashMap<String, *>>
                        M3ScoreMessageData.processData(messagesData)
                    }

                }
            }
        })
    }

    fun saveM3Assessment(record: M3Assessment) {

        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            return
        }

        val docRef = mFireStore.collection(M3ASSEMENT_COLLECTION)
            .document(user.userId)
            .collection(SCORE_COLLECTION)
            .document(record.id)

        debugLog(TAG, "Record to add : " + record.toString())
        debugLog(TAG, "Record added : " + record.id)
        debugLog(TAG, "Record added : " + record.createDate)
        docRef.set(record)
            .addOnSuccessListener { debugLog(TAG, "Completed store : SUCCESS") }
            .addOnFailureListener { e -> errorLog(TAG, "Error writing document" + e.toString()) }

        saveRecordToLocal(record)
    }


    private fun saveRecordToLocal(record: M3Assessment) {
        CoroutineScope(Dispatchers.IO).launch {
            mAppDb.m3assessmentDao().insert(record)
            debugLog(TAG, "M3Assessment :: INSERTED : " + record.allScore)
        }
    }


    fun fetchM3Assessments() {
        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            return
        }

        debugLog(TAG, "user.userId : " + user.userId)
        val docRef = mFireStore.collection(M3ASSEMENT_COLLECTION)
            .document(user.userId)
            .collection(SCORE_COLLECTION)

        docRef.get()
            .addOnSuccessListener { result ->
                val listRecords = ArrayList<M3Assessment>()
                for (document in result) {
                    Log.d("Assessment", "${document.id} => ${document.data}")
                    if (document.exists()) {
                        val record = document.toObject(M3Assessment::class.java)
                        record.synced = true
                        listRecords.add(record)
                    }
                }

                //Add this record to database
                saveDataToLocal(listRecords)
            }
            .addOnFailureListener { exception ->
                Log.d("Assessment", "Error getting documents: ", exception)
            }
    }

    fun getLastestAssessment()  =  assessmentDao.getByLatest()
    fun getLastestAssessmentBlocking()  =  assessmentDao.getByLatestBlocking()
    fun getAssessmentList() = assessmentDao.getAll()
    fun getListForMonth() = assessmentDao.getCurrentMonthList()

    fun getListForQuarter() = assessmentDao.getQuarterList()


}
