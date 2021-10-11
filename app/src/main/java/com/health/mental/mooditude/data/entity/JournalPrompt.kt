package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.health.mental.mooditude.data.model.journal.ApiJournalPrompt
import com.health.mental.mooditude.data.model.journal.JournalPromptStep
import java.lang.reflect.Type


/**
 * Created by Jayshree Rathod on 06,July,2021
 */
@Entity(tableName = "JournalPrompt")
data class JournalPrompt(
    @PrimaryKey
    var promptId: String = "",
    var groupId: String = "",
    var title: String = "",
    var imgStr: String = "",
    var position: Int = 0,
    var desc: String? = null,
    var isPremium: Boolean = false,
    var isActive: Boolean = true,

    var stepsStr: String? = null
) {

    companion object {

        fun getJournalPromptSteps(text: String): ArrayList<JournalPromptStep> {

            val listType: Type = object : TypeToken<ArrayList<JournalPromptStep?>?>() {}.type
            return Gson().fromJson(text, listType)
        }

        fun getJournalPromptStepString(steps: ArrayList<JournalPromptStep>): String {
            val gson = Gson()
            return gson.toJson(steps)
        }


        fun readFromApi(apiPrompt: ApiJournalPrompt): JournalPrompt? {
            if (apiPrompt.imgStr == null) {
                return null
            }
            val journalPrompt = JournalPrompt()
            journalPrompt.promptId = apiPrompt.id
            journalPrompt.groupId = apiPrompt.groupId
            journalPrompt.title = apiPrompt.title
            journalPrompt.imgStr = apiPrompt.imgStr!!
            journalPrompt.position = apiPrompt.order
            journalPrompt.isActive = apiPrompt.isActive
            journalPrompt.isPremium = apiPrompt.isPremium
            journalPrompt.desc = apiPrompt.desc
            journalPrompt.stepsStr = apiPrompt.steps?.let { getJournalPromptStepString(it) }

            return journalPrompt

        }

        fun toJournalPromptApi(journalPrompt: JournalPrompt): ApiJournalPrompt? {

            val apiPrompt = ApiJournalPrompt()
            apiPrompt.id = journalPrompt.promptId
            apiPrompt.groupId = journalPrompt.groupId
            apiPrompt.title = journalPrompt.title
            apiPrompt.imgStr = journalPrompt.imgStr

            apiPrompt.order = journalPrompt.position
            apiPrompt.isActive = journalPrompt.isActive
            apiPrompt.isPremium = journalPrompt.isPremium
            apiPrompt.desc = journalPrompt.desc
            if (journalPrompt.stepsStr != null) {
                apiPrompt.steps = getJournalPromptSteps(journalPrompt.stepsStr!!)
            }

            return apiPrompt

        }

        fun getUserinfo(prompt: JournalPrompt) =
            Gson().toJson(toJournalPromptApi(prompt))

        fun getPromptFromUserInfo(userInfo:String) =
            Gson().fromJson(userInfo, ApiJournalPrompt::class.java)


    }


}
