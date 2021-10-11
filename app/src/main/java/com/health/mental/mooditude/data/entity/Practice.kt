package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.health.mental.mooditude.data.model.ApiPractice

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
@Entity(tableName = "Practice")
data class Practice(@PrimaryKey
                    var practiceId: String
) {
    var name: String = ""
    var iconName: String = ""
    var iconIsImage: Boolean = false
    var duration: Int = 0 // In seconds
    var practiceDays: Int = 0
    var category: String = ""
    var order: Int = 0
    var isPremium: Boolean = false
    var isActive: Boolean = true

    var synced: Boolean = false
    var deleted: Boolean = false

    companion object {

        fun fromApiData(apiPractice: ApiPractice): Practice {
            val practice = Practice(apiPractice.id)
            practice.name = apiPractice.name
            practice.iconName = apiPractice.iconName
            practice.iconIsImage = apiPractice.iconIsImage
            practice.duration = apiPractice.duration
            practice.practiceDays = apiPractice.practiceDays
            practice.category = apiPractice.category
            practice.order = apiPractice.order
            practice.isPremium = apiPractice.isPremium
            practice.isActive = apiPractice.isActive


            return practice
        }

        fun toApiData(practice: Practice): ApiPractice {
            val apiPractice = ApiPractice(practice.practiceId)
            apiPractice.name = practice.name
            apiPractice.iconName = practice.iconName
            apiPractice.iconIsImage = practice.iconIsImage
            apiPractice.duration = practice.duration
            apiPractice.practiceDays = practice.practiceDays
            apiPractice.category = practice.category
            apiPractice.order = practice.order
            apiPractice.isPremium = practice.isPremium
            apiPractice.isActive = practice.isActive
            return apiPractice
        }
    }
}
