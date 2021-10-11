package com.health.mental.mooditude.data.model

/**
 * Created by Jayshree Rathod on 05,July,2021
 */
data class MeditationConfiguration (
    var inhaleDuration: Double = 5.0  ,        // seconds
    var exhaleDuration: Double = 5.0   ,       // seconds
    var inhaledPauseDuration: Double = 0.0,    // seconds
    var exhaledPauseDuration: Double = 0.0 ,   // seconds
    var backgroundMusicId: String = "boostPositiveEnergy"

){}