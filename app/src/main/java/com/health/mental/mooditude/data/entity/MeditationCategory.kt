package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.health.mental.mooditude.data.model.ApiMeditationCategory

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
@Entity(tableName = "MeditationCategory")
data class MeditationCategory(
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
    var attachment: String? = null,
    var expert:String? = null)
{

    companion object {
        fun readFromApi(apiMedicationCat: ApiMeditationCategory): MeditationCategory {
            val category = MeditationCategory(apiMedicationCat.categoryId)
            category.title = apiMedicationCat.title
            category.subtitle = apiMedicationCat.subtitle
            category.position = apiMedicationCat.order
            category.description = apiMedicationCat.desc
            category.imgStr = apiMedicationCat.imgStr
            category.isDarkImage = apiMedicationCat.isImageWithDarkTheme
            category.expert  = Gson().toJson(apiMedicationCat.expert)
            category.isActive = apiMedicationCat.isActive
            category.showCategoryCard = apiMedicationCat.showCategoryCard
            category.showExpertOnCard = apiMedicationCat.showExpertOnCard

            return category
        }

    }
}
