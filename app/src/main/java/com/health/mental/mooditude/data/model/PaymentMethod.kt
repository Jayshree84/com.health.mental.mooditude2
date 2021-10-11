package com.health.mental.mooditude.data.model

import android.content.Context
import com.health.mental.mooditude.R

/**
 * Created by Jayshree Rathod on 06,August,2021
 */
enum class PaymentMethod {
    unknown, insurance, medicare, cash;

    fun getTitle(context:Context):String {

        when(this) {
            unknown -> return context.getString(R.string.payment_method_unknown)
            insurance -> return context.getString(R.string.payment_method_insurance)
            medicare -> return context.getString(R.string.payment_method_medicare)
            cash -> return context.getString(R.string.payment_method_cash)
        }

    }
}
