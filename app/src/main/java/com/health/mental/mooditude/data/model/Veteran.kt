package com.health.mental.mooditude.data.model

import android.content.Context
import com.health.mental.mooditude.R

enum class Veteran {
    unknown, notVeteran, postNineEleven, preNineEleven;

    public fun getLocalizedString(context: Context): String {
        when (this) {
            unknown -> return context.getString(R.string.select_veteran_status)
            notVeteran -> return context.getString(R.string.not_veteran)
            postNineEleven -> return context.getString(R.string.post_nine_veteran)
            preNineEleven -> return context.getString(R.string.pre_nine_veteran)
        }
    }

    companion object {
        fun getArray(context: Context) =
             arrayOf(
                Veteran.unknown.getLocalizedString(context),
                 Veteran.notVeteran.getLocalizedString(context),
                 Veteran.postNineEleven.getLocalizedString(context),
                 Veteran.preNineEleven.getLocalizedString(context)
            )
    }
}
