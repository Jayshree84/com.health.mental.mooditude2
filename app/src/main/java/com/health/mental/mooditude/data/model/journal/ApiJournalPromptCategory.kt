package com.health.mental.mooditude.data.model.journal

import com.health.mental.mooditude.data.model.Expert

/**
 * Created by Jayshree Rathod on 05,July,2021
 */
data class ApiJournalPromptCategory(
    var categoryId: String = "",
    var title: String = "",
    var order: Int = 0,
    var subtitle: String? = null,
    var desc: String? = null,
    var imgStr: String? = null,
    var isImageWithDarkTheme : Boolean = false,

    var isActive: Boolean = true,
    var showCategoryCard: Boolean = false,
    var showExpertOnCard: Boolean = true,
    var attachment: String? = null,

    //Automatically deserialized
    var expert: Expert? = null
    )
{



}
