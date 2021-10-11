package com.health.mental.mooditude.data.model

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
data class ApiReward(
    var rewardId: Int = 0,
    var type : RewardType = RewardType.None,
    var description : String = "",
    var category : String?=null,
    var title : String? = null,
    var isActive: Boolean = true,
    var isPremium: Boolean = false,
    var byLine : String? = null,
    var isShown: Boolean = false,
    var isFavorite: Boolean = false,
    var language: String = "en",
    var order:Int = 0
)
