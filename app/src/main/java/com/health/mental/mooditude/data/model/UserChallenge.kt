package com.health.mental.mooditude.data.model

import android.content.Context
import com.health.mental.mooditude.R

enum class UserChallenge {
    people, work, health, money, me;

    fun getLocalizedString(context: Context): String {
        when (this) {
            people -> return context.getString(R.string.challenge_people)
            work -> return context.getString(R.string.challenge_work)
            health -> return context.getString(R.string.challenge_health)
            money -> return context.getString(R.string.challenge_money)
            me -> return context.getString(R.string.challenge_me)
        }
    }

    companion object {
        fun getArray(context: Context) =
            arrayOf(
                people.getLocalizedString(context),
                work.getLocalizedString(context),
                health.getLocalizedString(context),
                money.getLocalizedString(context),
                me.getLocalizedString(context)
            )
        fun getValues() =
            listOf(
                people,
                work,
                health,
                money,
                me
            )

    }
}
