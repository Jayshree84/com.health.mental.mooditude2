package com.health.mental.mooditude.data.model.community

/**
 * Created by Jayshree Rathod on 05,July,2021
 */
data class ApiPostCategory(
    var categoryId: String = "",
    var title: String = "",
    var desc: String? = null,
    var imgUrl: String? = null,
    var order: Int = 0,
    var isActive: Boolean = true,
    var isPrivate: Boolean = false,
    var isPremium: Boolean = false,
    var userCanPost:Boolean = true) {
}