package com.health.mental.mooditude.data.model.community

/**
 * Created by Jayshree Rathod on 07,September,2021
 */
enum class SortingType {
    new, popular, recentlyUpdated;

    fun getValue(): String {
        when(this) {
            new -> return "createdAt"
            popular -> return "activityCount"
            recentlyUpdated -> return "updatedAt"
        }

    }
}

/* case new = "createdAt" //Only will apply on Fresh (Fresh, none)
 case popular = "activityCount" // Only will apply on "Popular" (Popular, none)
 case recentlyUpdated = "updatedAt" //Will apply on each and every other filter.*/

/*var title: String? {
    return getLocalizedString(key: "POST_SORTING_\(self.rawValue)")
}

var desc: String? {
    return getLocalizedString(key: "POST_SORTING_DESC_\(self.rawValue)")
}

var bgImage: UIImage? {
    return nil
}

func getLocalizedString(key: String) -> String{
    return NSLocalizedString(key, tableName: "Community", bundle: Bundle.main, value: rawValue, comment: "")
}*/

