package com.health.mental.mooditude.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.dao.TherapistRequestDao
import com.health.mental.mooditude.data.entity.TherapistRequest
import com.health.mental.mooditude.data.model.*
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBTherapistRepository(
    private val mAppDb: AppDatabase,
    private val rdb: DatabaseReference,
    private val mFireStore: FirebaseFirestore
) {
    private val TAG = this.javaClass.simpleName
    private val COL_THERAPIST_REQUEST = "TherapistRequest"
    private val COL_USER_THERAPIST_REQUEST = "Requests"

    private var listener: ListenerRegistration? = null

    private val dao:TherapistRequestDao

    init {
        dao =  mAppDb.therapistRequestDao()
    }
    fun removeListener() {
        listener?.remove()
        listener = null
    }


    fun addListener() {

        val user = DataHolder.instance.getCurrentUser() ?: return

        // PATH in Firebase = TherapistRequest/{userId}/UserTherapistRequest/{requestId}
        val docRef = mFireStore.collection(COL_THERAPIST_REQUEST).document(user.userId)
            .collection(COL_USER_THERAPIST_REQUEST)

        listener = docRef.addSnapshotListener { value, error ->
            if (error != null) {
                errorLog(TAG, "Error in therapist request data : " + error.localizedMessage)
                return@addSnapshotListener
            }

            if (value != null) {
                val listData = ArrayList<ApiTherapistRequest>()
                val listRecords = ArrayList<TherapistRequest>()

                for (doc in value.documents) {
                    if (doc.exists()) {
                        val record = doc.toObject(ApiTherapistRequest::class.java)
                        if (record != null) {
                            listData.add(record)
                            listRecords.add(TherapistRequest.fromApiData(record))
                        }
                    }
                }

                //save locally
                //Add this record to database
                saveToLocal(listRecords)
            }
        }
    }

    private fun saveToLocal(list: ArrayList<TherapistRequest>) {
        CoroutineScope(Dispatchers.IO).launch {
            mAppDb.therapistRequestDao().insertAll(list)
            debugLog(TAG, "TherapistRequestData :: Stored :: Count :: " + list.size)
        }
    }

    fun uploadRequestTherapist(request: ApiTherapistRequest, listener: FBQueryCompletedListener) {

        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            listener.onResultReceived(null)
            return
        }

        request.requestId = mFireStore.collection(COL_THERAPIST_REQUEST).document(user.userId)
            .collection(COL_USER_THERAPIST_REQUEST).document().id
        debugLog(TAG, "Request ID is : " + request.requestId)

        val docRef = mFireStore.collection(COL_THERAPIST_REQUEST).document(user.userId)
            .collection(COL_USER_THERAPIST_REQUEST)
            .document(request.requestId!!)

        docRef.set(request)
            .addOnSuccessListener {
                debugLog(TAG, "Completed TherapistRequest : SUCCESS")
                listener.onResultReceived("success")
            }
            .addOnFailureListener { e ->
                errorLog(TAG, "ErrorTherapistRequest  writing document" + e.toString())
                listener.onResultReceived(e.localizedMessage)
            }
    }

    fun uploadRequestTherapistFeedback(
        requestId: String,
        feedback: TherapistFeedback,
        listener: FBQueryCompletedListener
    ) {
        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            return
        }

        val docRef = mFireStore.collection(COL_THERAPIST_REQUEST).document(user.userId)
            .collection(COL_USER_THERAPIST_REQUEST).document(requestId)

        docRef.update("feedback", feedback)
            .addOnSuccessListener {
                listener.onResultReceived(true)
                debugLog(TAG, "SUCCESS Feedback")
            }
            .addOnFailureListener { e ->
                listener.onResultReceived(null)
                debugLog(TAG, "Error Feedback : " + e.localizedMessage)
            }
    }

    fun getTherapistRequestList() = dao.getByLatest()

}