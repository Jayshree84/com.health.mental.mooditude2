package com.health.mental.mooditude.data.entity

/**
 * Created by Jayshree Rathod on 08,July,2021
 */

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Badges")
data class Badge(
    @PrimaryKey
    var day: Int = 0
) {
    var checkBadgeCount: Int = 0
    var starBadgeCount: Int = 0
    var crownBadgeCount: Int = 0
}

/*enum class BADGE {
    NONE,
    CHECK,
    STAR,
    CROWN
}*/


/*
@Entity(tableName = "Badge")
data class Badge(@PrimaryKey
                  val articleId: String = "") {

    var day: Int = 0
    var activity: String = ""
    var badge: BADGE = BADGE.NONE
    var synced: Boolean = false

}*/
