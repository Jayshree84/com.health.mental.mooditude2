package com.health.mental.mooditude.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

class RoomConverters {
    //for date and time convertions
    @TypeConverter
    fun calendarToDateStamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun dateStampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    /*//list of custom object in your database
    @TypeConverter
    fun saveAddressList(listOfString: List<AddressDTO?>?): String? {
        return Gson().toJson(listOfString)
    }

    @TypeConverter
    fun getAddressList(listOfString: String?): List<AddressDTO?>? {
        return Gson().fromJson(
            listOfString,
            object : TypeToken<List<String?>?>() {}.type
        )
    }
*/
    /*  for converting List<Double?>?  you can do same with other data type*/
    @TypeConverter
    fun saveDoubleList(listOfString: List<Double>): String? {
        return Gson().toJson(listOfString)
    }
}