package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.FirebaseStorageHelper
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.JournalPrompt
import com.health.mental.mooditude.data.entity.PromptCategory
import com.health.mental.mooditude.data.model.ApiEntry
import com.health.mental.mooditude.data.model.journal.ApiJournalPrompt
import com.health.mental.mooditude.data.model.journal.ApiJournalPromptCategory
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.createdEntryEvent
import com.health.mental.mooditude.services.instrumentation.deletedJournalEntry
import com.health.mental.mooditude.utils.CalendarUtils
import com.health.mental.mooditude.utils.removeAllNullFields
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBJournalRepository(
    private val mAppDb: AppDatabase,
    private val rdb: DatabaseReference, private val mFireStore: FirebaseFirestore
) {

    private val daoCat = mAppDb.journalPromptCatDao()
    private val daoPrompt = mAppDb.journalPromptDao()

    private val TAG = this.javaClass.simpleName
    private val COL_ENTRIES = "Journal"
    private val COL_USER_ENTRIES = "UserEntries"

    private val JOURNAL_PROMPTS_PATH = "journalPrompts/%s/"
    private val JOURNAL_PROMPTS_CAT_PATH = "journalPromptCategories/%s/"

    // private val repo = EntryRepository()
    private val entryRepository = EntryRepository(mAppDb)

    fun getJournalPrompts(language: String) {
        val path = String.format(JOURNAL_PROMPTS_PATH, language)
        val query1 = rdb.child(path)
        query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val list = ArrayList<ApiJournalPrompt>()
                    val listRecord = ArrayList<JournalPrompt>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val journalPrompt =
                            Gson().fromJson(json, ApiJournalPrompt::class.java)
                        journalPrompt.id = key as String
                        list.add(journalPrompt)

                        //Now save this data to db
                        val journalPromptRecord = JournalPrompt.readFromApi(journalPrompt)
                        if (journalPromptRecord != null) {
                            listRecord.add(journalPromptRecord)
                        }
                        // AppDatabase.articleDao()
                    }

                    //Call listener
                    insertAllPrompts(listRecord, true)
                }
            }
        })
    }

    fun fetchJournalCategories(language: String) {
        val path = String.format(JOURNAL_PROMPTS_CAT_PATH, language)
        val query1 = rdb.child(path)
        query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val list = ArrayList<ApiJournalPromptCategory>()
                    val list2 = ArrayList<PromptCategory>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val journalPromptCat =
                            Gson().fromJson(json, ApiJournalPromptCategory::class.java)
                        journalPromptCat.categoryId = key as String
                        list.add(journalPromptCat)


                        //Now save this data to db
                        val journalCatRecord =
                            PromptCategory.fromPromptCategoryApi(journalPromptCat)
                        list2.add(journalCatRecord)
                    }

                    //Now call listener
                    insertAllCats(list2, true)
                }
            }
        })
    }


    private var listener: ListenerRegistration? = null
    fun removeListener() {
        listener?.remove()
        listener = null
    }

    /// Fetch  user entries from server for the
    /*fun getEntries(start: Date, end: Date) {
        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            return
        }

        // PATH in Firebase = Entries/{userId}/userEntries
        val docRef =
            mFireStore.collection(COL_ENTRIES).document(user.userId).collection(COL_USER_ENTRIES)
                .whereGreaterThan("postedDate", start)
                .whereLessThan("postedDate", end)

        listener = docRef.addSnapshotListener { value, error ->
            processData(value, error)
        }
    }*/

    /// Fetch journal entries for logged in user. We add an observer, which keep an eye  on newly added
    /// or updated entries.
    fun addListener() {

        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            return
        }

        // Since we show summary for 3 month, we are keeping an eye for three month data on rolling basis
        val start = CalendarUtils.getStartOfMonth(System.currentTimeMillis(), 6)
        val end = CalendarUtils.getEndOfMonth(System.currentTimeMillis())

        // PATH in Firebase = Entries/{userId}/userEntries
        val docRef =
            mFireStore.collection(COL_ENTRIES).document(user.userId).collection(COL_USER_ENTRIES)
                .whereGreaterThanOrEqualTo("postedDate", Date(start))
                .whereLessThanOrEqualTo("postedDate", Date(end))

        listener = docRef.addSnapshotListener { value, error ->
            processData(value, error)
        }
        debugLog(TAG, "Listener added")
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

        debugLog(
            TAG,
            "JOURNAL , snapshot.metadata.hasPendingWrites() : " + snapshot.metadata.hasPendingWrites()
        )
        debugLog(TAG, "JOURNAL, snapshot.metadata.isFromCache : " + snapshot.metadata.isFromCache)
        if (snapshot.metadata.hasPendingWrites() /*|| snapshot.metadata.isFromCache*/) {
            return
        }
        val listRecordsToAdd = ArrayList<Entry>()
        val listEntriesToDelete = ArrayList<String>()
        val changes = snapshot.documentChanges
        for (change in changes) {
            if (!change.document.exists() || change.document.metadata.hasPendingWrites()) {
                continue
            }

            val doc = change.document
            debugLog(TAG, "Change Type : " + change.type)
            if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
                //debugLog(TAG, "Doc : " + doc.toString())
                val apiEntry = doc.toObject(ApiEntry::class.java)
                val record = Entry.fromApiData(apiEntry)
                record.synced = true
                listRecordsToAdd.add(record)
                debugLog(TAG, "Entry aded : " + apiEntry.toString())
            } else if (change.type == DocumentChange.Type.REMOVED) {
                listEntriesToDelete.add(doc.id)
                debugLog(TAG, "Entry removed : " + doc.id)
            }
        }

        debugLog(TAG, "Total records to add : " + listRecordsToAdd.size)
        debugLog(TAG, "Total records to delete : " + listEntriesToDelete.size)

        //Add this record to database
        if (listRecordsToAdd.size > 0) {
            saveEntriesToLocal(listRecordsToAdd)
        }

        if (listEntriesToDelete.size > 0) {
            deleteEntriesFromLocal(listEntriesToDelete)
        }
    }


    private fun saveEntriesToLocal(list: ArrayList<Entry>) {
        entryRepository.saveEntriesToLocal(list)
    }

    private fun deleteEntriesFromLocal(list: ArrayList<String>) {
        entryRepository.deleteEntriesFromLocal(list)
    }


    fun getDocRef(entry: Entry, userId: String): DocumentReference {
        debugLog(TAG, "Entry ID  : " + entry.entryId)
        return mFireStore.collection(COL_ENTRIES).document(userId).collection(COL_USER_ENTRIES)
            .document(entry.entryId)
    }


    fun saveEntry(entry: Entry, saveOnServer: Boolean = true) {
        entryRepository.saveRecord(entry)

        if (saveOnServer) {
            uploadJournalEntry(entry)
        }

        //logged an event
        EventCatalog.instance.createdEntryEvent(entry)
    }

    fun uploadJournalEntry(entry: Entry) {

        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            return
        }

        val docRef = getDocRef(entry, user.userId)

        val apiEntry = Entry.toApiData(entry)
        val entryToAdd = removeAllNullFields(apiEntry)
        debugLog(TAG, "entryToAdd : " + entryToAdd.toString())
        docRef.set(entryToAdd)
            .addOnCompleteListener {
                debugLog(TAG, "New entry added : " + entry.entryId)

                //Now mark as synced
                entry.synced = true
                entryRepository.markSynced(entry.entryId)
            }
            .addOnFailureListener {
                errorLog(TAG, "FAILURE : " + it.localizedMessage)
            }
    }


    fun getJournalCategories(): List<PromptCategory> {
        //daoCat.getAll()
        val callable = object : Callable<List<PromptCategory>> {
            override fun call(): List<PromptCategory> {
                return daoCat.getAll()
            }
        }

        val future: Future<List<PromptCategory>> =
            Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    fun getAllJournalPrompts(): List<JournalPrompt> {
        //daoPrompt.getAll()
        val callable = object : Callable<List<JournalPrompt>> {
            override fun call(): List<JournalPrompt> {
                return daoPrompt.getAll()
            }
        }

        val future: Future<List<JournalPrompt>> =
            Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }


    fun getPromptForOnboard(): JournalPrompt {
        //daoPrompt.getAll()
        val callable = object : Callable<JournalPrompt> {
            override fun call(): JournalPrompt {
                return daoPrompt.getById("stressToJoy_1")
            }
        }

        val future: Future<JournalPrompt> =
            Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }


    fun insertAllCats(list: List<PromptCategory>, removeAll: Boolean = false) {
        val callable = object : Callable<Any> {
            override fun call() {
                if (removeAll) {
                    //daoCat.deleteAll()
                }
                daoCat.insertAll(list)
                debugLog(TAG, "Table created : ")
            }
        }
        Executors.newSingleThreadExecutor().submit(callable)
    }

    fun insertAllPrompts(list: List<JournalPrompt>, removeAll: Boolean = false) {
        val callable = object : Callable<Any> {
            override fun call() {
                if (removeAll) {
                    //daoPrompt.deleteAll()
                    debugLog(TAG, "Removed all")
                }
                daoPrompt.insertAll(list)
                debugLog(TAG, "Table created : ")
            }
        }
        Executors.newSingleThreadExecutor().submit(callable)
    }

    fun getAllEntries(startDate: Long, endDate: Long) =
        entryRepository.getAllEntries(startDate, endDate)

    fun loadMultiple(startDate: Date, pageSize: Int) =
        entryRepository.loadMultiple(startDate, pageSize)

    fun deleteEntry(entry: Entry, listener:FBQueryCompletedListener) {

        //If user dont allow us to upload there data on server then return. (From Preferences)
        /*if !App.shared.preferences.syncWithServer{
            return
        }*/

        //If user not logged In then return.
        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            listener.onResultReceived(false)
            return
        }

        val docRef =
            mFireStore.collection(COL_ENTRIES).document(user.userId).collection(COL_USER_ENTRIES)
                .document(entry.entryId)
        docRef.delete()
            .addOnCompleteListener {
                debugLog(TAG, "entry deleted : " + entry.entryId)

                //Now mark as deleted
                entry.deleted = true
                entryRepository.markDeleted(entry.entryId)

                deleteRequiredEntryFiles(entry)
                listener.onResultReceived(true)

                //log an event
                EventCatalog.instance.deletedJournalEntry(entry)
            }
            .addOnFailureListener {
                errorLog(TAG, "FAILURE : " + it.localizedMessage)
                listener.onResultReceived(false)
            }
    }

    //In this func we will delete images/Videos which belongs to deleted Entry.
    private fun deleteRequiredEntryFiles(entry: Entry){

        val imgStr = entry.imageStr
        if(imgStr!= null) {
            if (imgStr.startsWith("http")) {
                FirebaseStorageHelper.instance.deleteFile(imgStr)
            }
        }

        val videoStr = entry.videoStr
        if(videoStr!= null) {
            if (videoStr.startsWith("http")) {
                FirebaseStorageHelper.instance.deleteFile(videoStr)
            }
        }

        /*if entry.attachmentType == .mirrorTalk{
            let mirrorTalk = entry.attachmentData as! CbtMirrorTalk

            if mirrorTalk.image != nil && mirrorTalk.videoUrl != nil{

                if mirrorTalk.image!.starts(with: "http"){
                FirebaseManager.shared.storageService.deleteAttachment(remotePath: mirrorTalk.image!)
            }

                if mirrorTalk.videoUrl!.starts(with: "http"){
                FirebaseManager.shared.storageService.deleteAttachment(remotePath: mirrorTalk.videoUrl!)
            }
            }
        }*/
    }
}