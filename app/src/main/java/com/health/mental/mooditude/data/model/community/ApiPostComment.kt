package com.health.mental.mooditude.data.model.community

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.Gson
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.dateFromUTC
import com.health.mental.mooditude.utils.dateToUTC
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jayshree Rathod on 05,July,2021
 */
@IgnoreExtraProperties
data class ApiPostComment(
    var postId: String? = null
) {
    private val TAG = this.javaClass.simpleName
    var commentId: String? = null
    var text: String = ""
    var postedBy: CommunityUser = CommunityUser.createCommunityUser()
    var createdAt: Date = Date()
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }

    var updatedAt: Date = Date()
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }


    //var media: ArrayList<ApiPost.Media> = ArrayList()
    var media: Any = ArrayList<ApiPost.Media>()
        get() {
            //debugLog("TAG", "Reactions :: " + Gson().toJson(field))
            try {
                //Let's first convert it to object
                return arrayListOf(Gson().fromJson(Gson().toJson(field), ApiPost.Media::class.java))
            } catch (e: Exception) {
                debugLog("ERROR", "ERROR :::: " + e.localizedMessage)

                //Then let's check for arraylist
                try {
                    val list =
                        Gson().fromJson(Gson().toJson(field), ArrayList<ApiPost.Media>().javaClass)
                    //debugLog(TAG, "list first element : " + list.get(0))
                    return list
                } catch (e: Exception) {
                    debugLog("ERROR", "ERROR :::: " + e.localizedMessage)
                    return ArrayList<ApiPost.Media>()
                }
            }
        }

    var isReport:Boolean = false

    var thumbsUp: ArrayList<String> = ArrayList()
    var thumbsDown: ArrayList<String> = ArrayList()


    @Exclude
    fun isThumbsUp(userId:String):Boolean {
        var index = thumbsUp.indexOf(userId)
        if(index >= 0) {
            thumbsUp.removeAt(index)
            return true
        }

        //Add in thumbs up
        thumbsUp.add(userId)
        index = thumbsDown.indexOf(userId)
        if(index >= 0){
            thumbsDown.removeAt(index)
        }
        return false
    }

    @Exclude
    fun isThumbsDown(userId:String):Boolean {
        var index = thumbsDown.indexOf(userId)
        if(index >= 0) {
            thumbsDown.removeAt(index)
            return true
        }

        //Add in thumbs down
        thumbsDown.add(userId)
        index = thumbsUp.indexOf(userId)
        if(index >= 0){
            thumbsUp.removeAt(index)
        }
        return false
    }

    @Exclude
    fun totalThumbs() = thumbsUp.size - thumbsDown.size

}