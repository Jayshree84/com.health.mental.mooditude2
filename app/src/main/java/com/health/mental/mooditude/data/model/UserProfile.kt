package com.health.mental.mooditude.data.model

/**
 * Created by Jayshree Rathod on 12,July,2021
 */
/**
This represent the public profile for a Moodituder
 */
data class UserProfile(var userId: String = "",
        var name: String = "",
        var photo: String = "",
        var stats: BadgeStat = BadgeStat(),
        var customerType: String = "free" // free, premium, awardee
)
