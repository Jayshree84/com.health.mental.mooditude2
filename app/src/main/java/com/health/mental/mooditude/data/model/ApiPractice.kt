package com.health.mental.mooditude.data.model

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
data class ApiPractice(
    var id: String = "",
    var name: String = "",
    var iconName: String = "",
    var iconIsImage: Boolean = false,
    var duration: Int = 0, // In seconds
    var order: Int = 0,
    //var practiceDays: OptionDay? = null,
    var practiceDays: Int = 0,
    var category: String = ApiPractice.CATEGORY_CUSTOM,
    var isRitual: Boolean = (category == ApiPractice.CATEGORY_RITUAL),
    var isUserCreated: Boolean = (category ==  ApiPractice.CATEGORY_CUSTOM),
    var isPremium: Boolean = false,
    var isActive: Boolean = true
)
{
    /*fun getPracticeDaysInt(): Int
    {
        if(practiceDays != null){
            return practiceDays!!.ordinal
        }
        return -1
    }*/



    companion object {
        const val CATEGORY_CUSTOM = "Custom"
        const val CATEGORY_RITUAL = "Ritual"

        fun getPracticeDayObj(day : Int):OptionDay? {
            return OptionDay.values().find { it.ordinal == day }
        }
    }
    enum class OptionDay {
        sunday,
        monday,
        tuesday,
        wednesday,
        thursday,
        friday,
        saturday,
        all
    }
}
