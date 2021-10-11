package com.health.mental.mooditude.data.model

import com.health.mental.mooditude.data.SharedPreferenceManager

/**
 * Created by Jayshree Rathod on 05,July,2021
 */
data class ApiServerConfiguration constructor(
    var meditationsVersion: Int = 0,
    var journalPromptsVersion: Int = 0,
    var forumCategoriesVersion: Int = 0,
    var jokeVersion: Int = 0,
    var quoteVersion: Int = 0,
    var affirmationVersion: Int = 0,
    var tipVersion: Int = 0,
    var coursesVersion: Int = 0,
    var m3AssessmentVersion: Int = 0,
    var practiceVersion: Int? = null,
    var statesVersion: Int? = null,
    var articleVersion: Int? = null,

    var meditationLanguages: String = "en",
    var journalPromptLanguages: String = "en",
    var forumCategoryLanguages: String = "en",
    var jokeLanguages: String = "en",
    var quoteLanguages: String = "en",
    var affirmationLanguages: String = "en",
    var tipLanguages: String = "en",
    var coursesLanguages: String = "en",
    var m3AssessmentLanguages: String = "en",
    var practiceLanguages: String? = null,

    //tipLanguages
    var stateLanguages: String? = null,
    var articleLanguages: String? = null
) {

    companion object {
        fun getExisting(): ApiServerConfiguration {
            val configuration = SharedPreferenceManager.getServerConfigurations()

            if (configuration != null) {
                return configuration
            }

            return ApiServerConfiguration()
        }

    }

    fun languageAvailability(languages: String): String {
        val language = SharedPreferenceManager.getLanguage()
        if(languages.contains(language)) {
            return language
        }

        return "en"
    }

    fun save() {
        SharedPreferenceManager.setServerConfigurations(this)
    }
}