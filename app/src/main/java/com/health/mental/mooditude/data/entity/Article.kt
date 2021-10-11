package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.health.mental.mooditude.data.model.ApiArticle


@Entity(tableName = "Articles")
data class Article(@PrimaryKey
                   val articleId: String = "") {

    var title: String? = ""
    var excrept: String? = ""
    var category: String? = ""
    var type: String? = ""
    var articleLinkStr: String? = ""
    var tags: String? = null

    var publishedData: String? = ""
    var modifiedOn:String? = ""
    var synced: Boolean = false
    var deleted: Boolean = false
    var isActive: Boolean = true
    var isPremium: Boolean = false
    var imgStr: String? = null
    var stepsStr: String? = null

    constructor(articleId: String, title: String, excrept: String, category: String, type: String, articleLinkStr: String) : this(articleId) {
        this.title = title
        this.excrept = excrept
        this.category = category
        this.type = type
        this.articleLinkStr = articleLinkStr
    }

    /*constructor(apiArticle: ApiArticle) : this(apiArticle.articleId) {
        //copy other contents
    }*/

    companion object {
        fun readFromApi(apiArticle: ApiArticle):Article? {
            if(apiArticle.imgStr == null) {
                return null
            }
            val article = Article(apiArticle.articleId)
            article.title = apiArticle.title
            article.excrept = apiArticle.excrept
            article.category = apiArticle.category
            article.type = apiArticle.type
            article.articleLinkStr = apiArticle.articleLinkStr
            article.publishedData = apiArticle.publishDate
            article.modifiedOn = apiArticle.modifiedOn
            article.synced = apiArticle.synced
            article.deleted = apiArticle.deleted
            article.imgStr = apiArticle.imgStr
            article.isActive = apiArticle.isActive
            article.isPremium = apiArticle.isPremium
            return article
        }
    }
}