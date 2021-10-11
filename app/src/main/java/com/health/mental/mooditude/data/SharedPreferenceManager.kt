package com.health.mental.mooditude.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.health.mental.mooditude.data.model.ApiServerConfiguration
import com.health.mental.mooditude.data.model.M3QuestionData
import com.health.mental.mooditude.data.model.M3ScoreMessageData
import com.health.mental.mooditude.data.model.MeditationConfiguration
import java.util.*


/**
 * Created by Jayshree.Rathod on 19-09-2017.
 */
object SharedPreferenceManager {

    //Shared Preference field used to save and retrieve data
    lateinit var preferences: SharedPreferences

    //private members
    private const val ID_SERVER_CONFIGURATIONS = "_SERVER_CONFIGURATIONS_"
    private const val ID_MEDITATION_CONFIGURATIONS = "_MEDITATION_CONFIGURATIONS_"
    private const val ID_QUESTIONS_DATA = "_M3QUESTIONS_DATA_"
    private const val ID_M3SCORE_MSG_DATA   =   "_M3SCORE_MSG_DATA_"

    //Unique device ID used while FCM registrations
    private const val ID_UNIQUE_ID = "_DEVICE_UNIQUE_ID_"

    //FCM Registration token
    private const val ID_FCM_TOKEN          =   "_FCM_REGISTRATION_TOKEN_"

    private const val ID_ASSESSMENT_COMPLETED_BY_UID          =   "_ASSESSMENT_COMPLETED_"

    private const val ID_AUTO_BACKUP          =   "_AUTO_BACKUP_"

    private const val ID_USER_LOGGED_IN     =   "_USER_ALREADY_LOGGED_IN_"

    private const val ID_SITUATION_INFO    =   "_SITUATION_INFO_"

    private const val ID_APP_INSTALLED  =   "_APP_INSTALLED_FIRSTTIME"

    //Name of Shared Preference file
    private const val PREFERENCES_FILE_NAME = "MOODY_PREFERENCES"

    /**
     * Call this first before retrieving or saving object.
     *
     * @param application Instance of application class
     */
    fun with(application: Application) {
        preferences = application.getSharedPreferences(
            PREFERENCES_FILE_NAME, Context.MODE_PRIVATE
        )
    }

    /**
     * Saves object into the Preferences.
     *
     * @param `object` Object of model class (of type [T]) to save
     * @param key Key with which Shared preferences to
     **/
    private fun <T> putObject(`object`: T, key: String) {
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(`object`)
        //printLog("Json string saved :: " + jsonString)
        //Save that String in SharedPreferences
        preferences.edit().putString(key, jsonString).apply()
    }

    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
    inline fun <reified T> getObject(key: String): T? {
        //We read JSON String which was saved.
        val value = preferences.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        if (value != null) {
            return GsonBuilder().create().fromJson(value, T::class.java)
        }
        return null
    }

    /**
     * Returns server configurations
     *
     * @param context
     * @return
     */
    fun getServerConfigurations() =
        getObject<ApiServerConfiguration>(ID_SERVER_CONFIGURATIONS)

    /**
     * Returns server configurations
     *
     * @param context
     * @return
     */
    fun setServerConfigurations(configuration: ApiServerConfiguration?) =
        putObject(configuration, ID_SERVER_CONFIGURATIONS)


    /**
     * Returns meditation configurations
     *
     * @param context
     * @return
     */
    fun getMeditationConfigurations() =
        getObject<MeditationConfiguration>(ID_MEDITATION_CONFIGURATIONS)

    /**
     * Returns Questions data
     *
     * @param context
     * @return
     */
    fun setQuestionsData(data: M3QuestionData) =
        putObject(data, ID_QUESTIONS_DATA)

    /**
     * Returns Questions data
     *
     * @param context
     * @return
     */
    fun getQuestionsData() =
        getObject<M3QuestionData>(ID_QUESTIONS_DATA)


    fun setM3ScoreMessageData(data: M3ScoreMessageData) =
        putObject(data, ID_M3SCORE_MSG_DATA)


    fun getM3ScoreMessageData() =
        getObject<M3ScoreMessageData>(ID_M3SCORE_MSG_DATA)


    fun setFCMToken(data: String) = putObject(data, ID_FCM_TOKEN)

    fun getFCMToken() = getObject<String>(ID_FCM_TOKEN)

    fun setDeviceId(data: String) = putObject(data, ID_UNIQUE_ID)

    fun getDeviceId() = getObject<String>(ID_UNIQUE_ID)

    /**
     * Returns meditation configurations
     *
     * @param context
     * @return
     */
    fun setMeditationConfigurations(configuration: MeditationConfiguration) =
        putObject(configuration, ID_MEDITATION_CONFIGURATIONS)

    fun getLanguage(): String {
        val language = Locale.getDefault().language
        if (language.startsWith("es")) {
            return "es"
        }
        return "en"
    }

    fun getAssessmentCompleted() =
        getObject<Boolean>(ID_ASSESSMENT_COMPLETED_BY_UID)

    fun setAssessmentCompleted(data: Boolean) =
        putObject(data, ID_ASSESSMENT_COMPLETED_BY_UID)

    fun getAutoBackup() = true
        //getObject<Boolean>(ID_AUTO_BACKUP)

    fun setAutoBackup(data: Boolean) =
        putObject(data, ID_AUTO_BACKUP)

    fun isUserAlreadyLoggedIn() = getObject<Boolean>(ID_USER_LOGGED_IN)
    fun setUserAlreadyLoggedIn(data: Boolean) = putObject(data, ID_USER_LOGGED_IN)

    fun getSituationInfoFlag() = getObject<Boolean>(ID_SITUATION_INFO)
    fun setSituationInfoFlag(flag: Boolean) = putObject(flag, ID_SITUATION_INFO)

    fun logOutUser() {
        setUserAlreadyLoggedIn(false)
        setAutoBackup(false)
        setAssessmentCompleted(false)
        setServerConfigurations(null)
        setSituationInfoFlag(false)
    }

    fun isAppAlreadyLaunched() = getObject<Boolean>(ID_APP_INSTALLED)
    fun setAppAlreadyLaunched() = putObject(true, ID_APP_INSTALLED)

}