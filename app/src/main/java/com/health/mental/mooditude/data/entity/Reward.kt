package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.health.mental.mooditude.data.model.ApiReward
import com.health.mental.mooditude.data.model.RewardType

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
@Entity(tableName = "Reward")
data class Reward(
    @PrimaryKey(autoGenerate = true)
    var rewardId: Int
) {
    var category: String? = null

    var type : RewardType = RewardType.None
    var title: String? = null
    var description: String = ""
    var byLine: String? = null
    var isShown: Boolean = false
    var isFavorite: Boolean = false
    var language: String = "en"
    var isActive: Boolean = true
    var isPremium: Boolean = false
    var order: Int = 0

    companion object {

        fun fromApiData(apiReward: ApiReward): Reward {
            val reward = Reward(apiReward.rewardId)
            reward.rewardId = apiReward.rewardId
            reward.category = apiReward.category
            reward.type= apiReward.type
            reward.title= apiReward.title
            reward.description= apiReward.description
            reward.byLine= apiReward.byLine
            reward.isShown= apiReward.isShown
            reward.isFavorite= apiReward.isFavorite
            reward.language= apiReward.language
            reward.isActive= apiReward.isActive
            reward.isPremium= apiReward.isPremium
            reward.order= apiReward.order

            return reward
        }

        fun toApiData(reward: Reward): ApiReward {
            val apiReward = ApiReward(reward.rewardId)
            apiReward.rewardId = reward.rewardId
            apiReward.category = reward.category
            apiReward.type= reward.type
            apiReward.title= reward.title
            apiReward.description= reward.description
            apiReward.byLine= reward.byLine
            apiReward.isShown= reward.isShown
            apiReward.isFavorite= reward.isFavorite
            apiReward.language= reward.language
            apiReward.isActive= reward.isActive
            apiReward.isPremium= reward.isPremium
            apiReward.order= reward.order

            return apiReward
        }
    }
}
