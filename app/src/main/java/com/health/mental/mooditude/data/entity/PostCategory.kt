package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.health.mental.mooditude.data.model.community.ApiPostCategory

/**
 * Created by Jayshree Rathod on 08,July,2021
 */
@Entity(tableName = "ForumCategory")
data class PostCategory(
    @PrimaryKey
    var categoryId: String,
    var title: String = ""
){
    var description: String? = null
    var imgStr: String? = null
    var position: Int = 0
    var isActive: Boolean = true
    var isPremium: Boolean = false
    var isPrivate: Boolean = false
    var userCanPost: Boolean = true

    companion object {

        fun fromApiData(apiCat: ApiPostCategory): PostCategory? {

            if (apiCat.categoryId == null ||
                apiCat.title == null ||
                apiCat.desc == null ||
                apiCat.order == null ||
                apiCat.imgUrl == null
            ) {
                return null
            }

            val category = PostCategory(apiCat.categoryId)
            category.title = apiCat.title
            category.position = apiCat.order
            category.description = apiCat.desc
            category.imgStr = apiCat.imgUrl
            category.isActive = apiCat.isActive
            category.isPrivate = apiCat.isPrivate
            category.isPremium = apiCat.isPremium
            category.userCanPost = apiCat.userCanPost

            return category
        }

        fun toApiData(category: PostCategory): ApiPostCategory {
            val apiCat = ApiPostCategory(category.categoryId)
            apiCat.title = category.title
            apiCat.desc = category.description
            apiCat.categoryId = category.categoryId
            apiCat.order = category.position
            apiCat.imgUrl = category.imgStr
            apiCat.isActive = category.isActive
            apiCat.isPrivate = category.isPrivate
            apiCat.isPremium = category.isPremium
            apiCat.userCanPost = category.userCanPost
            return apiCat
        }

        fun defaultCategory(title: String) = PostCategory("none", title)

        fun myPostsCategory() = PostCategory("myPosts", "My Posts")

        fun myBookmarksCategory() = PostCategory("myBookmarks", "My Bookmarks")

        fun reportedPostsCategory() = PostCategory("reportedPosts", "Reported Posts")
    }
}
