package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.health.mental.mooditude.data.model.ApiTherapistRequest
import com.health.mental.mooditude.data.model.ContactTime
import java.util.*

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
@Entity(tableName = "TherapistRequest")
data class TherapistRequest(
    @PrimaryKey
    val requestId:String
) {
    var postedDate = Date()
    var modifiedOn = Date()
    var synced: Boolean = false
    var deleted: Boolean = false
    var feedback: Boolean = false
    var bestTimeToContact: ContactTime = ContactTime.morning
    companion object {

        fun fromApiData(apiData: ApiTherapistRequest): TherapistRequest {

            val record = TherapistRequest(apiData.requestId!!)
            record.postedDate = apiData.postedDate
            record.modifiedOn = apiData.modifiedOn
            record.synced = apiData.synced
            record.deleted = apiData.deleted
            record.feedback = apiData.feedback!= null
            record.bestTimeToContact = ContactTime.fromValue(apiData.requestInfo.bestTimeToContact)
            return record
        }


    }
}
