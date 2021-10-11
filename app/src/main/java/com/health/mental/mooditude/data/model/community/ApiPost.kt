package com.health.mental.mooditude.data.model.community

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.Gson
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.dateFromUTC
import com.health.mental.mooditude.utils.dateToUTC
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jayshree Rathod on 05,July,2021
 */
@IgnoreExtraProperties
data class ApiPost(
    var postId: String? = null
) {
    private val TAG = this.javaClass.simpleName
    var title: String? = null
    var text: String = ""
    var category: String = "none"
    var postedBy: CommunityUser = CommunityUser.createCommunityUser()
    var commentCount: Int = 0
    var reactionCount: Int = 0
    var bookmarkCount: Int = 0
    var activityCount: Int = 0

    var createdAt: Date = Date(System.currentTimeMillis())
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }

    var updatedAt: Date = Date(System.currentTimeMillis())
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }

    var bookmarks: ArrayList<String> = ArrayList<String>()
    var pinned: Boolean = false
    var showOnMain: Boolean = false
    var status: PostStatus = PostStatus.active
    var anonymousPost: Boolean = false
    var categoryType: String? = null

    enum class PostStatus {
        active, blocked, reported
    }

    class Media {
        //image url
        var url: String = ""
        var type: String = ""
        var videoUrl: String? = null //video url
    }

    var media: ArrayList<Media> = ArrayList()

    class ReactionData {
        var hugs: ArrayList<String> = ArrayList()
        var reports: ArrayList<String> = ArrayList()
    }


    var reactions: Any = Any()
        get() {
            //debugLog("TAG", "Reactions :: " + Gson().toJson(field))
            try {
                return (Gson().fromJson(Gson().toJson(field), ReactionData::class.java))
            } catch (e: Exception) {
                debugLog("ERROR", "ERROR :::: " + e.localizedMessage)

                //Then let's check for arraylist
                try {
                    val list =
                        Gson().fromJson(Gson().toJson(field), ArrayList<Any>().javaClass)
                    debugLog(TAG, "list first element : " + list.get(0))

                    val text = list.get(0).toString() + "," + list.get(1).toString()
                    debugLog(TAG, "Text : " + text)
                    return (Gson().fromJson(Gson().toJson(text), ReactionData::class.java))
                } catch (e: Exception) {
                    debugLog("ERROR", "ERROR :::: " + e.localizedMessage)
                    return ReactionData()
                }
            }
        }

    @Exclude
    fun calculateReactionsCount(): Int {
        debugLog(TAG, "ID : " + this.postId)
        var count: Int = 0
        count += (reactions as ReactionData).hugs.size

        return count
    }

    @Exclude
    fun isCurrentUserReacted(reactionType: ReactionType = ReactionType.hug): Boolean {
        val user = DataHolder.instance.getCurrentUser()!!
        return (reactions as ReactionData).hugs.contains(user.userId)
        return false
    }

    @Exclude
    fun isCurrentUserBookmarked(): Boolean {
        val user = DataHolder.instance.getCurrentUser()!!
        return bookmarks.contains(user.userId)
    }

    @Exclude
    fun toggleBookMark() {
        val userID = DataHolder.instance.getCurrentUser()!!.userId
        val index = bookmarks.indexOfFirst { it == userID }
        if (index < 0) {
            bookmarks.add(userID)
            bookmarkCount++
        } else {
            bookmarks.removeAt(index)
            bookmarkCount--
        }
    }

    @Exclude
    fun toggleReaction() {
        val userID = DataHolder.instance.getCurrentUser()!!.userId
        val reaction1 = (reactions as ReactionData)
        val index = reaction1.hugs.indexOfFirst { it == userID }
        if (index < 0) {
            reaction1.hugs.add(userID)
            reactionCount++
        } else {
            reaction1.hugs.removeAt(index)
            reactionCount--
        }

        updatedAt = Date(System.currentTimeMillis())
    }

    @Exclude
    fun updateCommentCount(newCount: Int) {
        commentCount = newCount
        updatedAt = Date(System.currentTimeMillis())
    }

}