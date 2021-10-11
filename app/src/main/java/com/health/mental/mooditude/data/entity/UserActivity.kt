package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.encoders.annotations.Encodable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.health.mental.mooditude.data.model.ActivityGroup
import com.health.mental.mooditude.utils.CalendarUtils
import com.health.mental.mooditude.utils.dateFromUTC
import com.health.mental.mooditude.utils.dateToUTC
import java.util.*

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
@IgnoreExtraProperties
@Entity(tableName = "UserActivity")
data class UserActivity(
    @PrimaryKey
    var activityId: String = ""
) {
    var title : String = ""
    var imageName : String = ""

    var userCreated: Boolean = true

    var modifiedOn: Date = Date(System.currentTimeMillis())
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }

    var createdOn: Date = Date(System.currentTimeMillis())
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }

    //Do not add on firestore
    @com.health.mental.mooditude.custom.Exclude
    @get:Exclude
    var synced: Boolean = false

    var deleted: Boolean = false

    var group: ActivityGroup = ActivityGroup.other

    //number of times used
    //Do not add on firestore
    @com.health.mental.mooditude.custom.Exclude
    @get:Exclude
    var  count = 0


}
