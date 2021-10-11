package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.health.mental.mooditude.data.model.*
import com.health.mental.mooditude.data.model.journal.EmotionType
import com.health.mental.mooditude.data.model.journal.EntryAttachmentType
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.utils.dateFromUTC
import com.health.mental.mooditude.utils.dateToUTC
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
@IgnoreExtraProperties
@Entity(tableName = "Entry")
data class Entry(
    @PrimaryKey
    var entryId: String = "")
{
    @Ignore
    fun getUserActivityImages(): String {
        var text = ""
        for (image in activities) {
            text += " " + image.imageName
        }
        return text
    }

    @Ignore
    fun getUserActivityTitles(): String {
        var text = ""
        for (image in activities) {
            text+= image.title+","
        }
        return text.trim(',')
    }

    @Ignore
    fun getPostLength(): Int {

        var text: String = ""
        if (entryType == EntryType.mood || entryType == EntryType.course || entryType == EntryType.journal) {
            if (post != null) {
                text = post!!
            }
        } else if (entryType == EntryType.guidedJournal) {
            if (attachmentType == EntryAttachmentType.guidedJournal) {
                if (userInfo != null) {
                    val prompt = JournalPrompt.getPromptFromUserInfo(userInfo!!)
                    if (prompt.steps != null) {
                        for (step in prompt!!.steps!!) {
                            if (step != null && step.userInput != null) {
                                text = text + " (" + step.userInput + ")"
                            }
                        }
                    }
                }
            }
        } else if (entryType == EntryType.cbt) {
            /*if entryType == . cbt {

                text = ""

                text = text + (post ?? "")

                if (attachmentType == . evidence) {
                    if let data = cbtEvidence {

                        text = text + " \(data.answer1 ?? "")"
                        text = text + " \(data.answer2 ?? "")"
                        text = text + " \(data.answer3 ?? "")"
                    }

                } else if (attachmentType == . thinkingErrors) {
                    if let data = cbtThinkingError {
                        text = text + " \(data.balanceThoughts ?? "")"
                    }

                } else if (attachmentType == . mirrorTalk) {

                } else if (attachmentType == . lookback) {

                    if let lb = cbtLookback {
                        text = text + " \(lb.answer1 ?? "")"
                        text = text + " \(lb.answer2 ?? "")"

                    }
                } else if (attachmentType == . beSpecific) {

                    if let cb = cbtBeSpecific {
                        text = text + " \(cb.specificProblem ?? "")"

                    }
                } else if (attachmentType == . acceptance) {

                    if let cb = cbtAcceptance {
                        text = text + " \(cb.flaw ?? "")"
                        text = text + " \(cb.acceptance ?? "")"

                    }
                }
            }*/
        }

        val wordsAry = text.split("\\s+".toRegex()).toTypedArray()
        var totalWords = 0
        for (word in wordsAry) {
            if (word.trim().isNotEmpty()) totalWords++
        }

        return totalWords
    }

    @Ignore
    fun hasImage() = !imageStr.isNullOrEmpty()

    var synced: Boolean = false
    var deleted: Boolean = false
    var modifiedOn: Date = Date(System.currentTimeMillis())
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field =  Date(dateToUTC(value.time))
        }

    var postedDate: Date = Date(System.currentTimeMillis())
        /*get() = Date(dateFromUTC(field.time))
        set(value) {
            field =  Date(dateToUTC(value.time))
        }*/

    var emotion: EmotionType? = null
    var emotionIntensity: Int = 0
    var post : String? = null
    var entryType: EntryType = EntryType.mood

    // CBT Tools
    var attachmentType: EntryAttachmentType = EntryAttachmentType.none
    var userInfo: String? = null


    //comma seperated values
    var imageStr: String? = null
    var videoStr: String? = null
    var activities = ArrayList<UserActivity>()
    var feelings = ArrayList<UserFeeling>()

    companion object {
        fun fromApiData(apiEntry: ApiEntry): Entry {
            val entry = Entry()
            entry.entryId = apiEntry.entryId
            entry.postedDate = apiEntry.postedDate
            entry.emotion = apiEntry.emotionType
            entry.emotionIntensity = apiEntry.emotionIntensity
            entry.post = apiEntry.post
            entry.entryType = apiEntry.entryType
            entry.attachmentType = apiEntry.tool
            entry.imageStr = apiEntry.image
            entry.videoStr = apiEntry.video
            entry.deleted = apiEntry.deleted
            entry.modifiedOn = apiEntry.modifiedOn
            entry.activities = apiEntry.activities
            entry.feelings = apiEntry.feelings
            entry.userInfo = apiEntry.userInfo

            return entry
        }

        fun toApiData(entry: Entry): ApiEntry {
            val  apiEntry = ApiEntry()
            apiEntry.entryId = entry.entryId
            apiEntry.postedDate = entry.postedDate
            apiEntry.emotionType = entry.emotion
            apiEntry.emotionIntensity = entry.emotionIntensity
            apiEntry.post = entry.post
            apiEntry.entryType = entry.entryType
            apiEntry.tool = entry.attachmentType
            apiEntry.image = entry.imageStr
            apiEntry.video = entry.videoStr
            apiEntry.deleted = entry.deleted
            apiEntry.modifiedOn = entry.modifiedOn
            apiEntry.activities = entry.activities
            apiEntry.feelings = entry.feelings
            apiEntry.userInfo = entry.userInfo

            return apiEntry
        }
    }
}