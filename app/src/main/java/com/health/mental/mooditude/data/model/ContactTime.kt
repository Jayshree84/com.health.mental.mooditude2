package com.health.mental.mooditude.data.model

import android.content.Context
import com.health.mental.mooditude.R

enum class ContactTime {
    morning, afternoon, evening;

    public fun getLocalizedString(context: Context): String {
        when (this) {
            morning -> return context.getString(R.string.time_to_contact1)
            afternoon -> return context.getString(R.string.time_to_contact2)
            evening -> return context.getString(R.string.time_to_contact3)
        }
    }

    companion object {
        fun getArray(context: Context) =
            arrayOf(
                ContactTime.morning.getLocalizedString(context),
                ContactTime.afternoon.getLocalizedString(context),
                ContactTime.evening.getLocalizedString(context),
            )

        fun fromValue(text:String) :ContactTime {
            var contactTime = ContactTime.morning
            if(text.equals("Afternoon ( 12:00 pm — 4 pm EST)", true)) {
                contactTime = afternoon
            }
            if(text.equals("Evening( 4:00 pm — 8 pm EST)", true)) {
                contactTime = evening
            }
            return contactTime
        }
    }
}
