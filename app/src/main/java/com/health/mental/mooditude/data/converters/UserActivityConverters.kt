package com.health.mental.mooditude.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.health.mental.mooditude.data.entity.UserActivity


/**
 * Created by Jayshree Rathod on 19,August,2021
 */
class UserActivityConverters {

    /*var strategy: ExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return false
        }

        override fun shouldSkipField(field: FieldAttributes): Boolean {
            System.out.println("Filed : "+ field.name + " : " + field.annotations + " : " +
                    field.getAnnotation(Exclude::class.java) != null)
            return field.getAnnotation(Exclude::class.java) != null
        }
    }*/

    @TypeConverter
    fun listToJson(value: ArrayList<UserActivity>?) = Gson().toJson(value)

    /*@TypeConverter
    fun jsonToList(value: String): List<UserActivity>? {
        val type =
            object : TypeToken<ArrayList<UserActivity>>() {}.type
        return Gson().fromJson<ArrayList<UserActivity>>(value, type)
    }*/
    @TypeConverter
    fun jsonToList(value: String): ArrayList<UserActivity>? {
        val list = Gson().fromJson(value, Array<UserActivity>::class.java).toList()
        val list2 = ArrayList<UserActivity>(list)
        return list2
    }

}