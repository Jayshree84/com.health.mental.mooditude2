package com.health.mental.mooditude.data.model

import androidx.room.Ignore
import com.google.firebase.Timestamp
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.Exclude
import java.util.*

@IgnoreExtraProperties
data class Purchase (

    var transactionId: String = "",
    var transactionDate: Timestamp?  = null,
    var licenseType: String   = "Premium",     // premium
    var productType: String    = "",    // consumeable, non-consumeable, subscription
    var expiryDate: Timestamp? = null,     // when license will expire nil means never
    var status: String? = null,   // active, canceled, expired, gracePeriod
    var paymentType: String? = null
)
{
    @Ignore
    @Exclude
    fun getTransactionDate():Date? {
        if(this.transactionDate != null) {
            this.transactionDate!!.toDate()
        }
        return null
    }

    @Ignore
    @Exclude
    fun getExpiryDate():Date? {
        if(this.expiryDate != null) {
            this.expiryDate!!.toDate()
        }
        return null
    }
}
