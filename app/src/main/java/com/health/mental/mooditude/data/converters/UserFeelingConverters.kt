package com.health.mental.mooditude.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.health.mental.mooditude.data.model.UserFeeling

/**
 * Created by Jayshree Rathod on 19,August,2021
 */
class UserFeelingConverters {

    @TypeConverter
    fun listToJson(value: ArrayList<UserFeeling>?) = Gson().toJson(value)

    /*@TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<UserFeeling>::class.java).toList() */

    @TypeConverter
    fun jsonToList(value: String): ArrayList<UserFeeling>? {
        val list = Gson().fromJson(value, Array<UserFeeling>::class.java).toList()
        val list2 =  ArrayList<UserFeeling>(list)
        return list2
    }
}