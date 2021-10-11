package com.health.mental.mooditude.data.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jayshree Rathod on 10,July,2021
 */
data class AppUser(
    var userId: String = "",
    var name: String = "",
    var photo: String = "",
    var email: String = "",
    var topGoal: UserTopGoal? = null,
    //var topChallenges: ArrayList<UserChallenge> = ArrayList<UserChallenge>(),
    //Comma seperated valyes of UserChallenge object
    var topChallenges: String = "",
    var memberSince: Long = 0,
    var committedToSelfhelp: Boolean = false,
    var activatedReminderAtStartup: Boolean = false,
    var customerType: String = "Free", // free, premium, awardee
    var purchases: ArrayList<Purchase>? = null,
    var isAdmin: Boolean = false,
    var freshChatRestoreID: String? = null,
    var paymentType: String? = null, // Apple, Web etc.

    //var paymentMethod: PaymentMethod = PaymentMethod.unknown,
    var expiryDate: Long = 0L,
    var goingToTherapy: Boolean = false,
    var knowCbt: Boolean = false,
    //Value may have from [1, 2, 3, 4, 5]
    //Same field name is used while updating
    var ageGroup: Int = 0,
    var gender: Int = 0,
    var state: String = "",
    var phone: String? = null,
    var isParent: Boolean? = null,
    var isLGBTQ: Boolean? = null,

    var userAddress: UserAddress? = null,
    var culturalValues: CulturalValues? = null,
    var companyInfo: CompanyInfo? = null,
    var ethnicity: Ethnicity = Ethnicity.unknown,
    var religion: Religion = Religion.unknown,
    var veteranStatus: Veteran = Veteran.unknown,
    var stats: BadgeStat = BadgeStat(),
    var nps: Int = 0,
    var properties: UserProperty = UserProperty(),
    //Based on profile completion by application
    var profileCompleted: Boolean = false,
    //field to know the device platform on which user is logged in
    var devicePlatform: DevicePlatform = DevicePlatform.Android,

    var invitationCode: String = "",
    var bestTimeToContact: ContactTime = ContactTime.morning,

    var providerAttributes: TherapistProviderAttributes = TherapistProviderAttributes()
) {

    init {
        if (photo.equals("null")) {
            photo = ""
        }
    }

    constructor(fbUser: FirebaseUser) : this(fbUser.uid) {
        this.memberSince = System.currentTimeMillis()
        this.email = fbUser.email!!
        this.name = fbUser.displayName!!
        this.photo = fbUser.photoUrl.toString()
        if (photo.equals("null")) {
            this.photo = ""
        }
    }


    enum class CustomerType {
        TrialPeriod,
        UnSubscribed,
        SubscriptionInProgress,
        Subscribed,
        Free
    }

    enum class DevicePlatform {
        Android,
        iPhone
    }

    @Exclude
    fun updateSubscription(data: Map<String, Any>) {
        this.purchases = ArrayList()

        //first check for purchases key
        val ps = data.get("purchases")
        if (ps != null) {
            val array = ps as ArrayList<*>
            for (item in array) {
                val json = Gson().toJson(item)
                val purchase =
                    Gson().fromJson(json, Purchase::class.java)
                purchases!!.add(purchase)
            }
        } else if (data.get("grant") != null) {
            val json = Gson().toJson(data.get("grant"))
            val purchase =
                Gson().fromJson(json, Purchase::class.java)
            purchases!!.add(purchase)
        }
    }

    @Exclude
    fun getFirstName() = identifyNamesFromDisplayName()[0]

    @Exclude
    fun getLastName() = identifyNamesFromDisplayName()[1]

    @Exclude
    fun identifyNamesFromDisplayName(): List<String> {
        var list: List<String> = arrayListOf("", "")
        if (!name.isNullOrBlank()) {
            list = name.toString().split(" ")
            if (list.size < 2) {
                list = arrayListOf(name, "")
            } else if (list.size > 2) {
                val len: Int = list.size / 2
                var list2: List<String> = arrayListOf(
                    list.subList(0, len).joinToString(" "),
                    list.subList(len, list.size).joinToString(" ")
                )
                list = list2
            }
        }

        return list
    }


}