package com.health.mental.mooditude.data.model

import android.content.Context
import com.health.mental.mooditude.R

/**
 * Created by Jayshree Rathod on 17,August,2021
 */
enum class ActivityGroup {
    other,
    people,
    activities,
    events,
    food,
    sleep,
    drugs,
    internet;

        fun getLocalizedName(context: Context):String {
            when(this) {
                other -> return context.getString(R.string.activity_group_other)
                people -> return context.getString(R.string.activity_group_people)
                activities -> return context.getString(R.string.activity_group_activities)
                events -> return context.getString(R.string.activity_group_events)
                food -> return context.getString(R.string.activity_group_food)
                sleep -> return context.getString(R.string.activity_group_sleep)
                drugs -> return context.getString(R.string.activity_group_drugs)
                internet -> return context.getString(R.string.activity_group_internet)
            }

        }
}