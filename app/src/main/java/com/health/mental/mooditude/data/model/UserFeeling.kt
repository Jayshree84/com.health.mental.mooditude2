package com.health.mental.mooditude.data.model

import com.health.mental.mooditude.data.model.journal.FeelingGroup

/**
 * Created by Jayshree Rathod on 17,August,2021
 */
class UserFeeling {
    var feelingId: String = ""
    var title: String = ""
    var intensity: Int = 0
    var entryId: String? = null
    var reaccessedIntensity: Int? = null
    var group : FeelingGroup? = null
}