package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.Exclude
import com.health.mental.mooditude.R
import com.health.mental.mooditude.utils.CalendarUtils
import com.health.mental.mooditude.utils.dateFromUTC
import com.health.mental.mooditude.utils.dateToUTC
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
@IgnoreExtraProperties
@Entity(tableName = "M3Assessment")
data class M3Assessment(
    @PrimaryKey
    var id: String = "" // timestamp - start of the day
) {
    var createDate: Date = Date(System.currentTimeMillis())
        get() = Date(dateFromUTC(field.time))
        set(value) {
            field = Date(dateToUTC(value.time))
        }
    var rawData: String = ""
    var rawTimeToAnswer: String = ""
    var allScore: Int = 0
    var gatewayScore: Int = 0
    var depressionScore: Int = 0
    var gadScore: Int = 0
    var panicScore: Int = 0
    var socialAnxietyScore: Int = 0
    var ptsdScore: Int = 0
    var ocdScore: Int = 0
    var bipolarScore: Int = 0
    var pdfDoc: String? = null

    var synced: Boolean = false


    fun init(
        createDate: Date,
        rawData: String,
        rawTimeToAnswer: String,
        allScore: Int = 0,
        gatewayScore: Int = 0,
        depressionScore: Int = 0,
        gadScore: Int = 0,
        panicScore: Int = 0,
        socialAnxietyScore: Int = 0,
        ptsdScore: Int = 0,
        ocdScore: Int = 0,
        bipolarScore: Int = 0
    ) {

        this.createDate = createDate
        this.rawData = rawData
        this.rawTimeToAnswer = rawTimeToAnswer

        this.allScore = allScore
        this.depressionScore = depressionScore
        this.gatewayScore = gatewayScore
        this.gadScore = gadScore
        this.panicScore = panicScore
        this.socialAnxietyScore = socialAnxietyScore
        this.ptsdScore = ptsdScore
        this.ocdScore = ocdScore
        this.bipolarScore = bipolarScore
    }

    @Exclude
    @Ignore
    fun getAnxietyScore() :Int {
        val anxietyScore = gadScore + panicScore + socialAnxietyScore + ptsdScore + ocdScore
        return anxietyScore
    }

    @Ignore
    @Exclude
    fun getSelectedOptions() : ArrayList<Int> {
        val listOption = arrayListOf<Int>()

        val list =  rawData.trim(',').split(",")
        for(item in list) {
            listOption.add(item.toInt())
        }
        return listOption
    }

    @Ignore
    @Exclude
    fun getSelectionOptionNameIds() : ArrayList<Int> {
        val listOption = arrayListOf<Int>()

        val list =  rawData.trim(',').split(",")
        for(item in list) {
            var id = -1
            when(item.toInt()) {
                0 -> {
                    id = R.string.answer_not_at_all
                }
                1 -> {
                    id = R.string.answer_rarely
                }
                2 -> {
                    id = R.string.answer_sometime
                }
                3 -> {
                    id = R.string.answer_often
                }
                4 -> {
                    id = R.string.answer_most_time
                }
            }
            listOption.add(id)
        }
        return listOption
    }

    @Ignore
    @Exclude
    fun getSelectionOptionColorIds() : ArrayList<Int> {
        val listOption = arrayListOf<Int>()

        val list =  rawData.trim(',').split(",")
        for(item in list) {
            var id = -1
            when(item.toInt()) {
                0 -> {
                    id = R.color.brand_yellow
                }
                1 -> {
                    id = R.color.risk_unlikely
                }
                2 -> {
                    id = R.color.risk_low
                }
                3 -> {
                    id = R.color.risk_medium
                }
                4 -> {
                    id = R.color.risk_high
                }
            }
            listOption.add(id)
        }
        return listOption
    }

    companion object {


    }
}
