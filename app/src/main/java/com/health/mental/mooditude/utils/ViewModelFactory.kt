package com.health.mental.mooditude.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.health.mental.mooditude.data.api.ApiHelper
import com.health.mental.mooditude.room.RoomDBViewModel
import com.mindorks.example.coroutines.data.local.DatabaseHelper

class ViewModelFactory(private val apiHelper: ApiHelper, private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(RoomDBViewModel::class.java)) {
            return RoomDBViewModel(apiHelper, dbHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}