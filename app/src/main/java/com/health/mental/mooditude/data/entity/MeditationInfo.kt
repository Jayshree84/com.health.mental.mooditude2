package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.health.mental.mooditude.data.model.ApiMeditationInfo
import com.health.mental.mooditude.data.model.Expert
import com.health.mental.mooditude.data.model.MeditationConfiguration

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
@Entity(tableName = "Meditation")
data class MeditationInfo(@PrimaryKey
                   val meditationId: String = "",
    var groupId: String = "",
    var title: String = "",
    var imageStr: String = "",
    var position: Int = 0,
    var isActive: Boolean = true,
    var isPremium: Boolean = false,
    var imageIsDark:Boolean = true,
    var duration: Int = 5 ,//mins
    var backgroundMusicId: String = "boostPositiveEnergy",
    var isGuided: Boolean = false,
    var narration: String? = null,
    var expert: String? = null,

    // used for timer
    //Save on table
    var configuration: String = "",
    var benefits: String? = null,
    var technique: String? = null) {

    //private  var configurationObj: MeditationConfiguration? = null

    companion object {
        fun readFromApi(apiMedicationInfo: ApiMeditationInfo): MeditationInfo {
            val info = MeditationInfo(apiMedicationInfo.meditationId)
            info.groupId = apiMedicationInfo.groupId
            info.isActive = apiMedicationInfo.isActive
            info.isPremium = apiMedicationInfo.isPremium
            info.imageIsDark = apiMedicationInfo.imageIsDark
            info.duration = apiMedicationInfo.duration
            info.backgroundMusicId = apiMedicationInfo.backgroundMusicId
            info.isGuided = apiMedicationInfo.isGuided
            info.narration = apiMedicationInfo.narrationStr
            info.imageStr = apiMedicationInfo.imgStr
            info.benefits = apiMedicationInfo.benefits
            info.technique = apiMedicationInfo.technique
            info.configuration = apiMedicationInfo.configuration.toString()
            info.expert = apiMedicationInfo.expert.toString()
            info.title = apiMedicationInfo.title
            info.position = apiMedicationInfo.order
            return info
        }

        fun toMeditationApi(medInfo: MeditationInfo): ApiMeditationInfo {
            val info = ApiMeditationInfo(medInfo.meditationId)
            info.groupId = medInfo.groupId
            info.isActive = medInfo.isActive
            info.imageIsDark = medInfo.imageIsDark
            info.isPremium = medInfo.isPremium
            info.duration = medInfo.duration
            info.backgroundMusicId = medInfo.backgroundMusicId
            info.isGuided = medInfo.isGuided
            info.narrationStr = medInfo.narration
            info.imgStr = medInfo.imageStr
            info.benefits = medInfo.benefits
            info.technique = medInfo.technique

            //Gson parsing
            var json = Gson().toJson(medInfo.configuration)
            val meditationCategory = Gson().fromJson(json, MeditationConfiguration::class.java)
            info.configuration = meditationCategory

            info.expert = Expert.getExpertData(medInfo.expert)

            info.title = medInfo.title
            info.order = medInfo.position
            return info
        }


    }
}