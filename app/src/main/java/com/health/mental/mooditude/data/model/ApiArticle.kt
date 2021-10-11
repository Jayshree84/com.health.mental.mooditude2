package com.health.mental.mooditude.data.model

/**
 * Created by Jayshree Rathod on 03,July,2021
 */
data class ApiArticle(
    var articleId: String = "",
    var title: String? = "",
    var excrept: String? = "",
    var category: String? = "",
    var type: String? = "",
    var articleLinkStr: String? = "",
    var tags: String? = null,

    var publishDate: String? = "",
    var modifiedOn:String? = "",
    var synced: Boolean = false,
    var deleted: Boolean = false,
    var isActive: Boolean = true,
    var isPremium: Boolean = false,
    var imgStr: String? = null,
    var stepsStr: String? = null
) {

}
