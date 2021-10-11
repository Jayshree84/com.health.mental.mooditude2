package com.health.mental.mooditude.data.model

import android.content.Context
import com.health.mental.mooditude.R

enum class Ethnicity {
    unknown, caucasian, hispanic, africanAmerican, southAsian, eastAsian, caribbean, biOrMultiracial, other;

    public fun getLocalizedString(context: Context): String {
        when (this) {
            unknown -> {
                return context.getString(R.string.doesnt_matter)
            }
            caucasian -> {
                return context.getString(R.string.ethnicity_caucasian)
            }
            hispanic -> {
                return context.getString(R.string.ethnicity_hispanic)
            }
            africanAmerican -> {
                return context.getString(R.string.ethnicity_african_american)
            }
            southAsian -> {
                return context.getString(R.string.ethnicity_south_asian)
            }
            eastAsian -> {
                return context.getString(R.string.ethnicity_east_asian)
            }
            caribbean -> {
                return context.getString(R.string.ethnicity_caribbean)
            }
            biOrMultiracial -> {
                return context.getString(R.string.ethnicity_bi_or_multiracial)
            }
            other -> {
                return context.getString(R.string.ethnicity_other)
            }
        }
    }

    companion object {
        fun getArray(context: Context): Array<String> {
            //Let's take unknown as Doesn't matter
            return arrayOf(
                Ethnicity.unknown.getLocalizedString(context),
                Ethnicity.caucasian.getLocalizedString(context),
                Ethnicity.hispanic.getLocalizedString(context),
                Ethnicity.africanAmerican.getLocalizedString(context),
                Ethnicity.southAsian.getLocalizedString(context),
                Ethnicity.eastAsian.getLocalizedString(context),
                Ethnicity.caribbean.getLocalizedString(context),
                Ethnicity.biOrMultiracial.getLocalizedString(context),
                Ethnicity.other.getLocalizedString(context)
            )
        }
    }
}
