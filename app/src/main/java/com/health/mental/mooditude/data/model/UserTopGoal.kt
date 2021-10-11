package com.health.mental.mooditude.data.model

import android.content.Context
import com.health.mental.mooditude.R

/**
 * Created by Jayshree Rathod on 12,July,2021
 */
enum class UserTopGoal {
    sleepBetter,
    handleStress,
    masterDepression,
    overcomeAnxiety,
    controlAnger,
    boostSelfEsteem,
    liveHappier;

    //var title = String.format()

    fun getLocalizedString(context: Context): String {
        when (this) {
            sleepBetter -> return context.getString(R.string.USER_TOP_GOALS_SLEEPBETTER)
            handleStress -> return context.getString(R.string.USER_TOP_GOALS_HANDLE_STRESS)
            masterDepression -> return context.getString(R.string.USER_TOP_GOALS_MASTER_DEPRESSION)
            overcomeAnxiety -> return context.getString(R.string.USER_TOP_GOALS_OVERCOME_ANXIETY)
            controlAnger -> return context.getString(R.string.USER_TOP_GOALS_CONTROL_ANGER)
            boostSelfEsteem -> return context.getString(R.string.USER_TOP_GOALS_BOOST_SELF_ESTEEM)
            liveHappier -> return context.getString(R.string.USER_TOP_GOALS_LIVE_HAPPIER)
        }
    }

    companion object {
        fun getArray(context: Context) =
            arrayOf(
                UserTopGoal.sleepBetter.getLocalizedString(context),
                UserTopGoal.handleStress.getLocalizedString(context),
                UserTopGoal.masterDepression.getLocalizedString(context),
                UserTopGoal.overcomeAnxiety.getLocalizedString(context),
                UserTopGoal.controlAnger.getLocalizedString(context),
                UserTopGoal.boostSelfEsteem.getLocalizedString(context),
                UserTopGoal.liveHappier.getLocalizedString(context)
            )
    }
}
