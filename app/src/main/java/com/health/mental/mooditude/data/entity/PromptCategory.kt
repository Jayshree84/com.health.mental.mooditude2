package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.health.mental.mooditude.data.model.journal.ApiJournalPromptCategory
import com.health.mental.mooditude.data.model.Expert

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
@Entity(tableName = "PromptCategory")
data class PromptCategory(
    @PrimaryKey val categoryId: String = "",

    var title: String = "",
    //Here column name is position
    var position: Int = 0,
    var subtitle: String? = null,
    var description: String? = null,
    var imgStr: String? = null,
    var isDarkImage : Boolean = false,

    var isActive: Boolean = true,
    var showCategoryCard: Boolean = false,
    var showExpertOnCard: Boolean = true,
    var attachment: String? = null)
{
    //Not adding in table
    @Ignore
    private var expert: Expert? = null

    //Fetch expert object
    fun getExpertData(): Expert? {
        if(this.attachment != null) {
            val json = Gson().toJson(this.attachment)
            expert =  Gson().fromJson(json, Expert::class.java)
        }
        return expert
    }

    companion object {


        fun fromPromptCategoryApi(apiMedicationCat: ApiJournalPromptCategory): PromptCategory {
            val category = PromptCategory(apiMedicationCat.categoryId)
            category.title = apiMedicationCat.title
            category.subtitle = apiMedicationCat.subtitle
            category.position = apiMedicationCat.order
            category.description = apiMedicationCat.desc
            category.imgStr = apiMedicationCat.imgStr
            category.isDarkImage = apiMedicationCat.isImageWithDarkTheme
            category.expert  = apiMedicationCat.expert
            category.isActive = apiMedicationCat.isActive
            category.showCategoryCard = apiMedicationCat.showCategoryCard
            category.showExpertOnCard = apiMedicationCat.showExpertOnCard

            return category
        }

    }
}
