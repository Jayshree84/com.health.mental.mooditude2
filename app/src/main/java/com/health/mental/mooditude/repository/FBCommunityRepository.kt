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
import com.health.mental.mooditude.data.entity.PostCategory
import com.health.mental.mooditude.data.model.community.*
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.interectedWithPost
import com.health.mental.mooditude.utils.CalendarUtils
import com.health.mental.mooditude.utils.dateToUTC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBCommunityRepository(
    private val mAppDb: AppDatabase,
    private val rdb: DatabaseReference,
    private val mFireStore: FirebaseFirestore
) {

    private val TAG = this.javaClass.simpleName
    private val POST_CATEGORIES_PATH = "postCategories/%s/"

    private val COL_FEED_POSTS = "FeedPosts"
    private val COL_COMMENTS = "FeedComments"
    private val COL_REACTIONS = "FeedReactions"
    private val PAGE_SIZE = 25L

    private val categoryDao = mAppDb.postCategoryDao()

    fun fetchPostCategories(language: String) {
        val path = String.format(POST_CATEGORIES_PATH, language)
        val query1 = rdb.child(path)
        query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val list = ArrayList<ApiPostCategory>()
                    val list2 = ArrayList<PostCategory>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val postCat =
                            Gson().fromJson(json, ApiPostCategory::class.java)
                        postCat.categoryId = key as String
                        list.add(postCat)

                        //Now save this data to db
                        val record = PostCategory.fromApiData(postCat)
                        if (record != null) {
                            list2.add(record)
                        }
                    }

                    //Now save to database
                    saveDataToLocal(list2, true)
                }
            }
        })
    }


    private fun saveDataToLocal(list: ArrayList<PostCategory>, removeAll: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            if (removeAll) {
                categoryDao.deleteAll()
                debugLog(TAG, "Removed all")
            }
            categoryDao.insertAll(list)
            debugLog(TAG, "onPostCategoryReceived :: INSERTED : " + list.size)
        }
    }

    fun getPostCategories(): ArrayList<PostCategory> {
        //daoCat.getAll()
        val callable = object : Callable<ArrayList<PostCategory>> {
            override fun call(): ArrayList<PostCategory> {
                return ArrayList(categoryDao.getAll())
            }
        }

        val future: Future<ArrayList<PostCategory>> =
            Executors.newSingleThreadExecutor().submit(callable)
        return future.get();
    }


    private var listener: ListenerRegistration? = null
    fun removeListener() {
        listener?.remove()
        listener = null
    }

    fun addListener() {

        var docRef = mFireStore.collection(COL_FEED_POSTS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(10)

        val lastFetchDate = CalendarUtils.getUtcDateAddSeconds(15)
        docRef = docRef
            .whereGreaterThan("createdAt", lastFetchDate)

        listener = docRef.addSnapshotListener(
            com.google.firebase.firestore.EventListener { value, error ->
                processData(value, error)
            })
    }

    private fun processData(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val listPosts = ArrayList<ApiPost>()

        if (error != null || value == null) {
            errorLog(TAG, "NO DATA in processData")
            return
        }

        // we want only newly created posts
        val changes = value.getDocumentChanges(MetadataChanges.EXCLUDE)
        for (change in changes) {
            if (!change.document.exists() /*|| change.document.metadata.hasPendingWrites()*/) {
                continue
            }

            val doc = change.document
            //val doc = change
            //debugLog(TAG, "Change Type : " + change.type)

            val apiPost = doc.toObject(ApiPost::class.java)
            if (apiPost != null) {
                apiPost.postId = doc.id
                listPosts.add(apiPost)
            }
        }
        DataHolder.instance.setFeedPosts(listPosts)
    }

    //MARK: Get Posts
    private fun buildQuery(categoryId: String, orderBy: SortingType): Query {

        var docRef = mFireStore.collection(COL_FEED_POSTS)
            .orderBy(orderBy.getValue(), Query.Direction.DESCENDING)
            .whereIn("status", listOf(ApiPost.PostStatus.active, ApiPost.PostStatus.reported))

        if (!categoryId.equals("none")) {
            docRef = docRef.whereEqualTo("category", categoryId)
        }

        return docRef
    }

    private fun executeQuery(
        docRef: Query, listener: FBQueryCompletedListener, isFirstNull: Boolean = false,
        isLastNull: Boolean = false
    ) {

        docRef.get()
            .addOnSuccessListener { value ->
                if (value != null) {
                    val listPosts = ArrayList<ApiPost>()

                    // we want only newly created posts
                    val documents = value.documents
                    for (document in documents) {
                        if (!document.exists() /*|| change.document.metadata.hasPendingWrites()*/) {
                            continue
                        }

                        val apiPost = document.toObject(ApiPost::class.java)
                        if (apiPost != null) {
                            apiPost.postId = document.id
                            listPosts.add(apiPost)
                        }
                        debugLog(TAG, "Posts added :" + listPosts.size + " : " + apiPost!!.postId)
                    }

                    //Result
                    //block(posts, snapshot?.documents.first, snapshot?.documents.last)
                    var first: Any? = null
                    var last: Any? = null
                    if (value.documents.size > 0) {
                        if (!isFirstNull) {
                            first = value.documents.first()
                        }
                        if (!isLastNull) {
                            last = value.documents.last()
                        }
                        //debugLog(TAG, "Posts first :" + first + " : " + last)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        listener.onResultReceived(
                            hashMapOf(
                                Pair("list", listPosts),
                                Pair("first", first),
                                Pair("last", last)
                            )
                        )
                    }
                } else {
                    errorLog(TAG, "NO DATA for query : " + docRef.toString())
                    listener.onResultReceived(null)
                }
            }
            .addOnFailureListener { exception ->
                errorLog(TAG, "CommentData failed with " + exception.localizedMessage)
                listener.onResultReceived(null)
            }
    }

    fun getPostsBefore(
        beforeToken: Any?,
        categoryId: String,
        orderBy: SortingType,
        listener: FBQueryCompletedListener
    ) {

        var docRef = buildQuery(categoryId, orderBy)

        if (beforeToken != null) {
            //docRef = docRef.endAt(beforeToken)
            docRef = docRef.endAt((beforeToken as DocumentSnapshot))
            debugLog(
                TAG,
                "Before token id : " + (beforeToken as DocumentSnapshot).id + " : " + orderBy.getValue()
            )
        }
        docRef = docRef.limit(PAGE_SIZE)

        executeQuery(docRef, listener, false, true)
    }

    fun getPostsAfter(
        afterToken: Any?,
        categoryId: String,
        orderBy: SortingType,
        listener: FBQueryCompletedListener
    ) {

        var docRef = buildQuery(categoryId, orderBy)

        if (afterToken != null) {
            docRef = docRef.startAfter((afterToken as DocumentSnapshot))
            debugLog(
                TAG,
                "After token id : " + (afterToken as DocumentSnapshot).id + " : " + orderBy.getValue()
            )
        }
        docRef = docRef.limit(PAGE_SIZE)

        var isFirstNull = true
        if (afterToken == null) {
            isFirstNull = false
        }
        executeQuery(docRef, listener, isFirstNull, false)
    }

    fun getMyPosts(orderBy: SortingType, listener: FBQueryCompletedListener) {

        val userId = DataHolder.instance.getCurrentUserId()

        var docRef = mFireStore.collection(COL_FEED_POSTS)
            .orderBy(orderBy.getValue(), Query.Direction.DESCENDING)

        docRef = docRef.whereEqualTo("postedBy.userId", userId)

        executeQuery(docRef, listener, true, true)
    }

    fun getBookmarks(orderBy: SortingType, listener: FBQueryCompletedListener) {

        val userId = DataHolder.instance.getCurrentUserId()

        var docRef = mFireStore.collection(COL_FEED_POSTS)
            .orderBy(orderBy.getValue(), Query.Direction.DESCENDING)

        docRef = docRef.whereArrayContains("bookmarks", userId)

        executeQuery(docRef, listener, true, true)
    }

    fun getReportedPosts(orderBy: SortingType, listener: FBQueryCompletedListener) {

        var docRef = mFireStore.collection(COL_FEED_POSTS)
            .orderBy(orderBy.getValue(), Query.Direction.DESCENDING)

        docRef = docRef.whereEqualTo("status", ApiPost.PostStatus.reported)

        executeQuery(docRef, listener, true, true)
    }

    fun getPinnedPosts(listener: FBQueryCompletedListener) {

        val docRef = mFireStore.collection(COL_FEED_POSTS)
            .whereEqualTo("pinned", true)

        executeQuery(docRef, listener, true, true)
    }


    fun getPostComments(postId: String, listener: FBQueryCompletedListener) {

        val docRef = mFireStore.collection(COL_COMMENTS)
            .whereEqualTo("postId", postId)
            .orderBy("updatedAt")

        docRef.get()
            .addOnSuccessListener { value ->
                if (value != null) {
                    // we want only newly created posts
                    val documents = value.documents
                    val listComments = ArrayList<ApiPostComment>()
                    for (document in documents) {
                        if (!document.exists()) {
                            continue
                        }

                        val comment = document.toObject(ApiPostComment::class.java)!!
                        comment.commentId = document.id
                        listComments.add(comment)
                    }

                    listener.onResultReceived(listComments)
                } else {
                    errorLog(TAG, "NO COMMENT DATA : ")
                    listener.onResultReceived(null)
                }
            }
            .addOnFailureListener { exception ->
                errorLog(TAG, "getpostcomments failed with " + exception.localizedMessage)
                listener.onResultReceived(null)
            }
    }

    fun savePost(post: ApiPost) {
        var id = post.postId
        if (id == null) {
            id = mFireStore.collection(COL_FEED_POSTS).document().id
        }
        val docRef = mFireStore.collection(COL_FEED_POSTS).document(id)

        if (post.postId == null) { // Create New Post
            post.postId = id

            docRef.set(post)
                .addOnCompleteListener {
                    debugLog(TAG, "New post added : " + post.postId)
                }
                .addOnFailureListener {
                    errorLog(TAG, "FAILURE : " + it.localizedMessage)
                }

        } else { // Update existing post

            var title = ""
            if (post.title != null) {
                title = post.title!!
            }
            post.updatedAt = Date(System.currentTimeMillis())
            val data = hashMapOf<String, Any>(
                Pair("title", title),
                Pair("text", post.text),
                Pair("media", post.media),
                Pair("anonymousPost", post.anonymousPost),
                Pair("category", post.category),
                Pair("updatedAt", post.updatedAt)
            )
            docRef.update(data)
                .addOnFailureListener {
                    errorLog(TAG, "Error in update post")
                }
                .addOnSuccessListener {
                    debugLog(TAG, "Success in create post")
                }
        }
    }

    fun saveComment(comment: ApiPostComment) {

        val user = DataHolder.instance.getCurrentUser()!!
        assert(comment.postedBy.userId == user.userId)

        var isNew = true
        var docRef = mFireStore.collection(COL_COMMENTS).document()
        if (comment.commentId != null) {
            isNew = false
            docRef = mFireStore.collection(COL_COMMENTS).document(comment.commentId!!)
        }

        comment.commentId = docRef.id

        docRef.set(comment)
            .addOnCompleteListener {
                debugLog(TAG, "New comment added : " + comment.commentId)
                if (isNew) {

                    if (!comment.isReport) {
                        incrementCommentCount(comment.postId!!)
                        EventCatalog.instance.interectedWithPost(comment.postId!!, "commented")
                    }
                    else {
                        EventCatalog.instance.interectedWithPost(comment.postId!!, "reported")
                    }
                }

            }
            .addOnFailureListener {
                errorLog(TAG, "FAILURE : " + it.localizedMessage)
            }
    }

    fun incrementCommentCount(
        postId: String,
        commentCount: Int = 1,
        commentReactionCount: Int = 0
    ) {

        mFireStore.runTransaction {
            try {
                val docRef = mFireStore.document(COL_FEED_POSTS + "/" + postId)
                val doc = it.get(docRef).data

                if (doc != null) {
                    var currCommentCount = 0L
                    if (doc.get("commentCount") != null) {
                        currCommentCount = doc.get("commentCount") as Long
                    }
                    var activityCount = currCommentCount
                    if (doc.get("activityCount") != null) {
                        activityCount = doc.get("activityCount") as Long
                    }

                    val data = hashMapOf<String, Any>(
                        Pair("commentCount", currCommentCount + commentCount),
                        Pair("activityCount", activityCount + commentReactionCount),
                        Pair("updatedAt", Date(dateToUTC(System.currentTimeMillis())))
                    )

                    it.update(docRef, data)
                    debugLog(TAG, "Updated comment count")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun thumbsUpDownComment(postId: String, commentId: String, thumbsUp: Boolean, remove: Boolean) {

        val userId = DataHolder.instance.getCurrentUser()!!.userId

        val docRef = mFireStore.collection(COL_COMMENTS).document(commentId)

        var firstField = "thumbsUp"
        var secondField = "thumbsDown"

        if (!thumbsUp) {
            firstField = "thumbsDown"
            secondField = "thumbsUp"
        }

        val data = HashMap<String, Any>()

        if (remove) {
            data[firstField] = FieldValue.arrayRemove(userId)
        } else {
            data[firstField] = FieldValue.arrayUnion(userId)
            data[secondField] = FieldValue.arrayRemove(userId)
        }

        docRef.update(data)
            .addOnCompleteListener {
                debugLog(TAG, "Thumbs up success: " + commentId)
                if (!remove) {
                    incrementCommentCount(postId, 0, 1)
                }
                EventCatalog.instance.interectedWithPost(postId, "thumbsUpDown")
            }
            .addOnFailureListener {
                errorLog(TAG, "FAILURE : " + it.localizedMessage)
            }
    }

    fun addOrRemoveBookmark(postId: String, listener: FBQueryCompletedListener) {
        mFireStore.runTransaction({
            try {
                val docRef = mFireStore.document(COL_FEED_POSTS + "/" + postId)
                val doc = it.get(docRef)

                val post = doc.toObject(ApiPost::class.java)
                if (post != null) {
                    //add postId
                    post.postId = doc.id
                    val userId = DataHolder.instance.getCurrentUser()!!.userId
                    val index = post.bookmarks.indexOfFirst { it == userId }

                    var change = 1
                    var fieldValue = FieldValue.arrayUnion(userId)

                    if (index >= 0) {
                        change = -1
                        fieldValue = FieldValue.arrayRemove(userId)
                        post.bookmarks.removeAt(index)
                    } else {
                        post.bookmarks.add(userId)
                    }
                    val data = hashMapOf<String, Any>(
                        Pair("bookmarkCount", post.bookmarkCount + change),
                        Pair("activityCount", post.activityCount + change),
                        Pair("bookmarks", fieldValue),
                        Pair("updatedAt", Date(dateToUTC(System.currentTimeMillis())))
                    )

                    it.update(docRef, data)
                    debugLog(TAG, "Updated bookmark count")
                    post.bookmarkCount = post.bookmarkCount + change
                    post.activityCount = post.activityCount + change
                    post.updatedAt = Date(System.currentTimeMillis())
                    listener.onResultReceived(post)
                    EventCatalog.instance.interectedWithPost(postId, "AddedRemovedBookmark")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).continueWith {
            val result = it.result
        }
    }


    fun addOrRemoveReaction(
        postId: String,
        type: ReactionType,
        listener: FBQueryCompletedListener?
    ) {
        mFireStore.runTransaction {
            try {
                val docRef = mFireStore.document(COL_FEED_POSTS + "/" + postId)
                val doc = it.get(docRef)

                val post = doc.toObject(ApiPost::class.java)
                if (post != null) {
                    post.postId = doc.id
                    val userId = DataHolder.instance.getCurrentUser()!!.userId

                    val reactions = (post.reactions as ApiPost.ReactionData)
                    val index = reactions.hugs.indexOfFirst { it == userId }

                    var hugFieldValue = FieldValue.arrayUnion(userId)
                    var change = 1

                    if (index != -1) {
                        hugFieldValue = FieldValue.arrayRemove(userId)
                        change = -1
                        reactions.hugs.removeAt(index)
                    } else {
                        reactions.hugs.add(userId)
                    }

                    val data = hashMapOf<String, Any>(
                        Pair("reactionCount", post.reactionCount + change),
                        Pair("activityCount", post.activityCount + change),
                        Pair("reactions.hugs", hugFieldValue),
                        Pair("updatedAt", Date(dateToUTC(System.currentTimeMillis())))
                    )

                    it.update(docRef, data)
                    debugLog(TAG, "Updated reactions data")
                    post.reactionCount = post.reactionCount + change
                    post.activityCount = post.activityCount + change
                    post.updatedAt = Date(System.currentTimeMillis())
                    post.reactions = reactions
                    if (listener != null) {
                        listener.onResultReceived(post)
                    }
                    EventCatalog.instance.interectedWithPost(postId, "AddedRemovedHug")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addReport(postId: String, listener: FBQueryCompletedListener) {

        mFireStore.runTransaction {
            try {
                val docRef = mFireStore.document(COL_FEED_POSTS + "/" + postId)
                val doc = it.get(docRef)

                val post = doc.toObject(ApiPost::class.java)
                if (post != null) {
                    post.postId = doc.id
                    val userId = DataHolder.instance.getCurrentUser()!!.userId
                    val index =
                        (post.reactions as ApiPost.ReactionData).reports.indexOfFirst { it == userId }

                    //If index found, means already reported, so just return
                    if (index >= 0) {
                        return@runTransaction
                    }

                    val reactions = (post.reactions as ApiPost.ReactionData)
                    val reportFieldValue = FieldValue.arrayUnion(userId)
                    reactions.reports.add(userId)

                    val data = hashMapOf<String, Any>(
                        Pair("reactions.reports", reportFieldValue),
                        Pair("status", ApiPost.PostStatus.reported),
                        Pair("updatedAt", Date(dateToUTC(System.currentTimeMillis())))
                    )

                    it.update(docRef, data)
                    debugLog(TAG, "Updated reactions data")
                    post.status = ApiPost.PostStatus.reported
                    post.updatedAt = Date(System.currentTimeMillis())
                    post.reactions = reactions
                    if (listener != null) {
                        listener.onResultReceived(post)
                    }
                    //listener.onResultReceived(post)
                    EventCatalog.instance.interectedWithPost(postId, "reported")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun blockPost(postId: String) {
        val docRef = mFireStore.collection(COL_FEED_POSTS).document(postId)
        docRef.update("status", ApiPost.PostStatus.blocked)
    }

    fun removeReportFromPost(postId: String) {
        val docRef = mFireStore.collection(COL_FEED_POSTS).document(postId)
        docRef.update("status", ApiPost.PostStatus.active)
    }

    fun getSinglePost(postId: String, listener: FBQueryCompletedListener) {
        debugLog(TAG, "Get single post for ID : " + postId)
        val docRef = mFireStore.collection(COL_FEED_POSTS).document(postId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    debugLog(TAG, "DocumentSnapshot data: ${document.data}")
                    val apiPost = document.toObject(ApiPost::class.java)
                    if(apiPost != null) {
                        apiPost.postId = document.id
                        listener.onResultReceived(apiPost)
                    }
                } else {
                    errorLog(TAG, "Error in get post for id : " + postId)
                    listener.onResultReceived(null)
                }
            }
            .addOnFailureListener { exception ->
                errorLog(TAG, "get failed with " + exception.localizedMessage)
                listener.onResultReceived(null)
            }
    }

    fun deletePost(postId: String, listener: FBQueryCompletedListener) {

        //First fetch post from id
        getSinglePost(postId, object : FBQueryCompletedListener {
            override fun onResultReceived(result: Any?) {
                if (result != null && result is ApiPost) {
                    val post = result as ApiPost
                    val mediaToBeDeleted = post.media

                    //First delete media
                    for (media in mediaToBeDeleted) {
                        FirebaseStorageHelper.instance.deleteFile(media.url)
                    }

                    //now delete the post
                    val docRef = mFireStore.collection(COL_FEED_POSTS).document(postId)
                    docRef.delete()
                    deleteComments(postId)
                }
                listener.onResultReceived(true)

                EventCatalog.instance.interectedWithPost(postId, "deleted")
            }
        })
    }


    fun deleteComments(postId: String) {

        val docRef = mFireStore.collection(COL_COMMENTS)
            .whereEqualTo("postId", postId)

        docRef.get()
            .addOnSuccessListener { value ->
                if (value != null) {
                    // we want only newly created posts
                    val documents = value.documents
                    for (document in documents) {
                        if (!document.exists()) {
                            continue
                        }

                        val comment = document.toObject(ApiPostComment::class.java)!!
                        val media1 = comment.media as ArrayList<ApiPost.Media>
                        for (media in media1) {
                            FirebaseStorageHelper.instance.deleteFile(media.url)
                        }
                        val docRef1 = mFireStore.collection(COL_COMMENTS).document(document.id)
                        docRef1.delete()
                    }
                } else {
                    errorLog(TAG, "NO COMMENT DATA : ")
                }
            }
            .addOnFailureListener { exception ->
                errorLog(TAG, "CommentData failed with " + exception.localizedMessage)
            }
    }
}

