package com.health.mental.mooditude.data.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

/**
 * Created by Jayshree Rathod on 12,August,2021
 */
@IgnoreExtraProperties
data class ApiTherapistRequest(
    var requestId: String? = null,
    var postedDate: Date = Date(),
    var modifiedOn: Date = Date(),
    var synced: Boolean = false,
    var deleted: Boolean = false,

    var requestInfo: TherapistRequestInfo = TherapistRequestInfo(),
    //var therapistPrefrence: TherapistPreference? = null,

    //var hasFeedback: Boolean = false,
    var feedback: TherapistFeedback? = null,

    //var isNew : Boolean = true,

    //var assessment: M3Assessment? = null
) {
}