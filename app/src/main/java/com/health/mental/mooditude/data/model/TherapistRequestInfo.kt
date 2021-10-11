package com.health.mental.mooditude.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.Exclude

/**
 * Created by Jayshree Rathod on 11,August,2021
 */
@IgnoreExtraProperties
class TherapistRequestInfo() {
    var name: String = ""
    var email:String = ""
    var ageGroup:String = ""
    var gender:String = ""
    var veteranStatus:String = "Unknown" //Veteran.unknown
    var state:String = ""
    var phone:String = ""
    var bestTimeToContact: String = ""//ContactTime.morning

    var assessmentScore:Int = 0

    var listPreferences: ArrayList<String> = ArrayList()

    var providerAttributes = TherapistProviderAttributes()

    var paymentMethod: String = "Don’t know yet" // PaymentMethod.unknown

    var comment: String = ""

}