package com.health.mental.mooditude.data.model

import com.google.gson.annotations.SerializedName
import com.health.mental.mooditude.data.entity.UserActivity
import com.health.mental.mooditude.data.model.journal.EmotionType
import com.health.mental.mooditude.data.model.journal.EntryAttachmentType
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.utils.dateFromUTC
import com.health.mental.mooditude.utils.dateToUTC
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jayshree Rathod on 17,August,2021
 */
@com.google.firebase.firestore.IgnoreExtraProperties
data class ApiEntry(
    var entryId: String = "",
    var emotionType: EmotionType? = null,
    var emotionIntensity: Int = 0,
    var post: String? = "",
    var entryType: EntryType = EntryType.mood,
    var tool: EntryAttachmentType = EntryAttachmentType.none,
    var image: String? = null,
    @SerializedName("video")
    var video: String? = null,
    var deleted: Boolean = false,

    var activities: ArrayList<UserActivity> = ArrayList<UserActivity>(),
    var feelings: ArrayList<UserFeeling> = ArrayList<UserFeeling>(),
    var userInfo: String? = null
    //var mirrorTalkImage: String? = null,
    //var mirrorTalkVideo: String? = null,
) {

    var postedDate: Date = Date(System.currentTimeMillis())
        /*get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }*/

    var modifiedOn: Date = Date(System.currentTimeMillis())
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }

}