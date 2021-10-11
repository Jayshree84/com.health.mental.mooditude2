package com.health.mental.mooditude.core

import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.data.model.AppUser
import com.health.mental.mooditude.data.model.UserProfile
import com.health.mental.mooditude.data.model.community.ApiPost

/**
 * Created by Jayshree Rathod on 10,July,2021
 */
class DataHolder private constructor() {

    //used for logging purpose
    protected val TAG = this.javaClass.simpleName

    private object Holder {
        val INSTANCE = DataHolder()
    }

    companion object {
        val instance: DataHolder by lazy { Holder.INSTANCE }
    }

    private var mDeviceId: String = ""
    private var mFCMToken: String = ""
    private var mCurrentUser: AppUser? = null
    private var mCurrentUserProfile: UserProfile? = null

    private var mListCommunityPost: ArrayList<ApiPost> = ArrayList()

    fun getCurrentUser(): AppUser? {
        return mCurrentUser
    }

    fun getCurrentUserId(): String {
        return mCurrentUser!!.userId
    }

    fun setCurrentUser(user: AppUser?) {
        this.mCurrentUser = user
    }

    fun getCurrentUserProfile(): UserProfile? {
        return mCurrentUserProfile
    }

    fun setCurrentUserProfile(userProfile: UserProfile?) {
        this.mCurrentUserProfile = userProfile
    }

    fun getDeviceId() = mDeviceId
    fun setDeviceId(id: String) {
        mDeviceId = id
    }

    fun getFCMToken() = mFCMToken
    fun setFCMToken(id: String) {
        mFCMToken = id
    }

    fun logOut() {
        //Let's first set firebasetoken to empty
        //FirebaseDBManager.instance.removeFirebaseRegistrationToken()
        this.mCurrentUser = null
        this.mCurrentUserProfile = null

        //Remove all sharedpreference value
        SharedPreferenceManager.logOutUser()
    }

    fun setFeedPosts(listPosts: ArrayList<ApiPost>) {
        mListCommunityPost = listPosts
    }

    fun getFeedPosts() = mListCommunityPost


}