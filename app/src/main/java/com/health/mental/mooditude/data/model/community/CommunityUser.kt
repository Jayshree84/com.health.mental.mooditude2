package com.health.mental.mooditude.data.model.community

import android.content.Context
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.DataHolder

/**
 * Created by Jayshree Rathod on 31,August,2021
 */
@IgnoreExtraProperties
class CommunityUser {
    var userId: String = ""
    var name: String? = null
    var photo: String? = null
    var customerType: String = "free"


    companion object {
        fun createCommunityUser(): CommunityUser {
            val currUser = DataHolder.instance.getCurrentUser()!!
            val communityUser = CommunityUser()
            communityUser.userId = currUser.userId
            communityUser.name = currUser.name
            communityUser.customerType = currUser.customerType
            communityUser.photo = currUser.photo
            return communityUser
        }
    }

    @Exclude
    fun isCurrentUser(): Boolean {
        if (DataHolder.instance.getCurrentUser()!!.userId.equals(userId)) {
            return true
        }
        return false
    }

    @Exclude
    fun getDisplayName(context: Context): String {
        if (isCurrentUser()) {
            return context.getString(R.string.you)
        }
        if (name == null) {
            return context.getString(R.string.anonymous)
        }
        return name!!
    }

    @Exclude
    fun getPhotoUrl(): String {

        if (isCurrentUser()) {
            return DataHolder.instance.getCurrentUser()!!.photo
        }
        if (photo == null) {
            return "anonymous"
        }

        return photo!!
    }
}

