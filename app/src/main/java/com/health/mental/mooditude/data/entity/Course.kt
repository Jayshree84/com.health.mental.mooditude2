package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.health.mental.mooditude.data.model.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
@Entity(tableName = "Course")
data class Course(
    @PrimaryKey
    var courseId: String = "",
    var title: String = "",
    var description: String = "",
    var imgStr: String = "",
    var category: String = "",
    var position: Int = 0,
    var isActive: Boolean = true,
    var isPremium: Boolean = true,
    var heroImageIsDark: Int = 0,
    var expert: String? = null,
    var lessons:String = "",
    var lessonsStr: String? = null,
    var appStoreId: String? = null,
    var isFeatured: Boolean = false,
    var version: Int = 0,
    var publishedDate: Date? =  null,
    var availabilityDate: Date? = null
)
{
    //ArrayList<Lesson>

    fun getExpertData(): Expert? {
        return Expert.getExpertData(this.expert)
    }

    fun getLession(): HashMap<String, Lesson> {
        var lessonsArry = HashMap<String, Lesson>()

        val gson = Gson()
        val listType: Type = object : TypeToken<HashMap<String, Lesson>?>() {}.type
        lessonsArry = Gson().fromJson(this.lessons, listType)
        return lessonsArry
    }

    companion object {
        fun fromApiData(apiCourse: ApiCourse): Course {
            val course = Course(
                apiCourse.courseId,
                apiCourse.title,
                apiCourse.description,
                apiCourse.heroImage,
                apiCourse.category,
                apiCourse.order
            )

            course.isActive = apiCourse.isActive
            course.isPremium = apiCourse.isPremium
            course.appStoreId = apiCourse.appStoreId
            course.version = apiCourse.version
            course.isFeatured = apiCourse.isFeatured
            course.heroImageIsDark = apiCourse.heroImageBrightness
            course.publishedDate = apiCourse.publishedDate
            course.availabilityDate = apiCourse.availabilityDate
            course.expert = apiCourse.expert.toString()
            course.lessons = Gson().toJson(apiCourse.lessons)

            return course
        }

        fun toApiData(course: Course): ApiCourse {
            val apiCourse = ApiCourse(
                course.courseId,
                course.title,
                course.description,
                course.imgStr,
                course.heroImageIsDark
            )

            apiCourse.isActive = course.isActive
            apiCourse.isPremium = course.isPremium
            apiCourse.appStoreId = course.appStoreId
            apiCourse.version = course.version
            apiCourse.isFeatured = course.isFeatured
            apiCourse.heroImageBrightness = course.heroImageIsDark
            apiCourse.publishedDate = course.publishedDate
            apiCourse.availabilityDate = course.availabilityDate
            apiCourse.expert = course.getExpertData()
            apiCourse.lessons = course.getLession()

            return apiCourse
        }
    }
}