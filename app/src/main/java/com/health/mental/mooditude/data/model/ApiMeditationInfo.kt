package com.health.mental.mooditude.data.model

import com.health.mental.mooditude.data.SharedPreferenceManager

/**
 * Created by Jayshree Rathod on 05,July,2021
 */
data class ApiMeditationInfo (
    var meditationId: String = "",
    var groupId: String = "",
    var title: String = "",
    var order: Int = 0,
    var isActive: Boolean = true,
    var isPremium: Boolean = false,
    var duration: Int = 180,// seconds
    var backgroundMusicId: String = "boostPositiveEnergy",
    var isGuided: Boolean = false,
    var imgStr: String = "",
    var narrationStr: String? = null,
    var expert: Expert? = null,
    var imageIsDark:Boolean = true,


    // used for timer
    var configuration: MeditationConfiguration? = null,
    var benefits: String? = null,
    var technique: String? = null,


    /**
     * URL Object creates issue on GsonParsing
     */
    /// Return the image URL for this meditation
    //var imageUrl: URL? = URL(imageStr)

    /// Return URL of meditation narration (guided meditation)
    //var narration: URL? = URL(narrationStr)
) {
    /// If a user has configured a meditation, returns that value else returns default meditation configuration
    fun getCustomizedConfiguration(): MeditationConfiguration {
        val configuration = SharedPreferenceManager.getMeditationConfigurations()
        if (configuration != null) {
            return configuration
        }

        //Default values
        return MeditationConfiguration()
    }

    fun saveConfiguration(configuration: MeditationConfiguration) {
        SharedPreferenceManager.setMeditationConfigurations(configuration)
    }
}