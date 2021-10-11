package com.health.mental.mooditude.data.model.journal

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R

/**
 * Created by Jayshree Rathod on 17,August,2021
 */
enum class EmotionType {
    worst, low, normal, high, elevated;

    fun getImage(context: Context): Drawable? {
        when(this) {
            worst-> return ContextCompat.getDrawable(context, R.drawable.mood_worst)
            low-> return ContextCompat.getDrawable(context, R.drawable.mood_low)
            normal-> return ContextCompat.getDrawable(context, R.drawable.mood_normal)
            high-> return ContextCompat.getDrawable(context, R.drawable.mood_high)
            elevated-> return ContextCompat.getDrawable(context, R.drawable.mood_elevated)
        }

    }
}