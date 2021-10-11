package com.health.mental.mooditude.listener

import com.health.mental.mooditude.adapter.UserActivityIconAdapter

/**
 * Created by Jayshree Rathod on 15,July,2021
 */
interface ActivityIconSelectListener {
    abstract fun onIconSelected(adapter: UserActivityIconAdapter, imageName: String)
}