package com.health.mental.mooditude.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.health.mental.mooditude.R
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.receiver.AlarmReceiver
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Jayshree Rathod on 23,July,2021
 */
object CalendarUtils {

    private val TAG = javaClass.simpleName

    fun getStartTimeOfDay() = startOfDay(System.currentTimeMillis())
    fun getEndTime(time: Long) = endOfDay(time)

    fun getPreviousMonthTime(time: Long): Long {

        debugLog(TAG, "Date : " + SimpleDateFormat("dd MM yyyy", Locale.US).format(time))
        val month = Calendar.getInstance()
        month.timeInMillis = time

        //Previous month
        if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {
            month.set((month.get(Calendar.YEAR) - 1), month.getActualMaximum(Calendar.MONTH))
        } else {
            month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1)
        }

        //debugLog(TAG, "Date : " + SimpleDateFormat("dd MM yyyy", Locale.US).format(startOfDay(month.timeInMillis)))
        return startOfDay(month.timeInMillis)
    }

    private fun startOfDay(time: Long): Long {

        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(Calendar.HOUR_OF_DAY, 0) //set hours to zero
        cal.set(Calendar.MINUTE, 0) // set minutes to zero
        cal.set(Calendar.SECOND, 0) //set seconds to zero
        cal.set(Calendar.MILLISECOND, 0) //set seconds to zero
        Log.i("Time", cal.getTime().toString())
        return cal.timeInMillis
    }

    private fun endOfDay(time: Long): Long {

        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(
            Calendar.HOUR_OF_DAY,
            cal.getActualMaximum(Calendar.HOUR_OF_DAY)
        ) //set hours to zero
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE)) // set minutes to zero
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND)) //set seconds to zero
        cal.set(
            Calendar.MILLISECOND,
            cal.getActualMaximum(Calendar.MILLISECOND)
        ) //set seconds to zero
        Log.i("End Time", cal.getTime().toString())
        return cal.timeInMillis

    }

    /*fun getUTCTime(): Long {
        return dateToUTC(System.currentTimeMillis())
    }

    fun getUTCTime(time: Long): Long {
        return dateToUTC(time)
    }*/

    private fun getEpochTime(): Long {
        val epochTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        val ts = String.valueOf(epochTime)
        System.out.println("epoch time" + ts)
        return epochTime
    }

    fun formatDateForAssessment(createDate: Date): kotlin.String {
        val simpleDateFormat = SimpleDateFormat("MMMM dd, yyyy")
        return simpleDateFormat.format(createDate)
    }

    fun getDay(date: Date): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.time = date

        debugLog(TAG, "Day" + cal.get(Calendar.DAY_OF_MONTH))
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(date: Date): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.time = date

        debugLog(TAG, "MONTH" + cal.get(Calendar.MONTH))
        return cal.get(Calendar.MONTH)
    }

    fun getDiffInDays(startDate: Date, endDate: Date): Long {
        val millionSeconds = endDate.time - startDate.time
        val days = TimeUnit.MILLISECONDS.toDays(millionSeconds)
        debugLog(TAG, "Diff days : " + days)
        return days
    }

    fun getDiffInHours(startDate: Date, endDate: Date): Long {
        val millionSeconds = endDate.time - startDate.time
        val hours = TimeUnit.MILLISECONDS.toHours(millionSeconds)
        debugLog(TAG, "Diff HOURS : " + hours)
        return hours
    }

    fun getNextDay(date: Date): kotlin.String {
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        var tomorrow = ""

        debugLog(TAG, "Present Day" + cal.get(Calendar.DAY_OF_WEEK))
        val today = cal.get(Calendar.DAY_OF_WEEK)

        var nextDay = today + 1
        if (today == Calendar.FRIDAY || today == Calendar.SATURDAY || today == Calendar.SUNDAY) {
            nextDay = Calendar.MONDAY
        }
        cal.set(Calendar.DAY_OF_WEEK, nextDay)
        tomorrow = SimpleDateFormat("EEEE", Locale.US).format(cal.timeInMillis)
        return tomorrow
    }

    fun getStartTime(date: Date) = startOfDay(date.time)

    fun getStartOfMonth(time: Long, prevMonths: Int): Long {
        var startDate = time
        for (index in 1..prevMonths) {
            startDate = getPreviousMonthTime(startDate)
        }

        return getStartOfMonth(startDate)
    }

    fun getStartOfMonth(time: Long): Long {
        val date = Date(time)
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        cal.set(
            Calendar.DAY_OF_MONTH,
            cal.getActualMinimum(Calendar.DAY_OF_MONTH)
        ) //set hours to zero
        Log.i("Time", cal.getTime().toString())
        return cal.timeInMillis
    }

    fun getEndOfMonth(time: Long): Long {
        val date = Date(time)
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        cal.set(
            Calendar.DAY_OF_MONTH,
            cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        ) //set hours to zero
        Log.i("Time", cal.getTime().toString())
        return cal.timeInMillis
    }

    fun getUtcDateAddSeconds(noOfSeconds: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, noOfSeconds)
        return Date(dateToUTC(calendar.timeInMillis))

    }

    fun getGreetingsText(context:Context): kotlin.String {
        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]
        return if (timeOfDay < 12) {
            context.getString(R.string.greeting_morning)
        } else if (timeOfDay < 16) {
            context.getString(R.string.greeting_afternoon)
        } else if (timeOfDay < 21) {
            context.getString(R.string.greeting_evening)
        } else {
            context.getString(R.string.greeting_night)
        }
    }

    /**
     * Set alarm for score card notification
     */
    fun setReminderForUser(context: Context) {

        val intent = Intent(context, AlarmReceiver::class.java)

        val bundle = Bundle()
        bundle.putBoolean("reminder", true)
        intent.putExtra("bundle", bundle)

        val id = ALARM_ID_FOR_USER_REMINDER

        //next day random hour
        val target: Calendar = Calendar.getInstance()

        val hourOfday = getRandomNumber(7,21)
        debugLog(TAG, "hour of day for reminder : " + hourOfday)
        target.set(Calendar.HOUR_OF_DAY, hourOfday)
        target.set(Calendar.MINUTE, 0)
        target.set(Calendar.SECOND, 0)

        //set next day - tomorrow
        target.add(Calendar.DAY_OF_YEAR, 1);

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        debugLog(TAG, "Set timezone to : " + TimeZone.getDefault().toString() + " : " + TimeZone.getDefault().displayName)

        if (target.timeInMillis == 0L) {
            alarmManager.cancel(pendingIntent)
            debugLog(TAG, "Alarm is cancel : ")
        } else {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.timeInMillis, pendingIntent)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, target.timeInMillis, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, target.timeInMillis, pendingIntent)
            }*/
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, target.timeInMillis, 1000 * 60 * 60 * 24, pendingIntent)
            debugLog(TAG, "Alarm is set to target : " + target.timeInMillis)
            debugLog(TAG, "Alarm is set to target : " + SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.US).format(target.timeInMillis))
        }
    }

    /**
     * Cancel Alarms
     */
    fun cancelAlarmForUser(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)

        val id = ALARM_ID_FOR_USER_REMINDER

        val pendingIntent1 = PendingIntent.getBroadcast(context, id, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent1)
    }

}