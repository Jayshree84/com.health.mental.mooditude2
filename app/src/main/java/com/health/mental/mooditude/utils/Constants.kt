package com.health.mental.mooditude.utils

/**
 * Created by Jayshree Rathod on 02,July,2021
 */
/**
 * Constants used throughout the app.
 */

//splash timeout period
const val SPALSH_TIME_OUT: Long = 3000 //2 seconds

const val DATABASE_NAME = "moody-db"
const val ARTICLE_DATA_FILENAME = "Articles.json"
const val MEDITATION_CATEGORY_FILENAME = "MeditationCategory.json"
const val MEDITATION_INFO_FILENAME = "Meditation.json"
const val USER_ACTIVITY_FILENAME = "UserActivity.json"
const val PROMPT_CATEGORY_FILENAME = "PromptCategory.json"
const val JOURNAL_PROMPT_FILENAME = "JournalPrompt.json"

const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101
const val REQUEST_ID_CAPTURE_IMAGE = 201
const val REQUEST_ID_SELECT_IMAGE = 202


const val REQUEST_ID_START_REGISTRATION = 205
const val REQUEST_ID_START_ONBOARDING = 206
const val REQUEST_ID_START_ASSESSMENT_STAGE1 = 207
const val REQUEST_ID_START_ASSESSMENT_STAGE2 = 208
const val REQUEST_ADD_POST_COMMENT = 209
const val REQUEST_ADD_POST_COMMENT_TO_REPORT = 210
const val REQUEST_EDIT_POST = 211
const val REQUEST_EDIT_ENTRY = 212
const val REQUEST_CREATE_MOOD_ENTRY = 213
const val REQUEST_CREATE_GUIDED_ENTRY = 214

const val REQUEST_SIGNUP_ONBOARDING = 215
const val REQUEST_POST_DETAILS = 216
const val REQUEST_ADD_NEW_POST = 217

const val REQUEST_ID_START_ASSESSMENT = 218

const val RESULT_POST_REPORTED = 500
const val RESULT_POST_DELETED = 501
const val RESULT_POST_EDITED = 502
const val RESULT_ASSESSMENT_FINISHED    =   503



const val PARAM_QUESTION = "Question"

const val ASSESSMENT_EXPIRY_DAYS = 15  //15 days


const val DATE_FORMAT_MOOD_TIME = "MMMM dd, yyyy hh:mm aa"
const val DATE_FORMAT_JOIN = "MMM dd, yyyy"

const val KEY_EXIT = "exit_app"

const val KEY_FCM_POST_ID = "postId"

const val ALARM_ID_FOR_USER_REMINDER = 1001

const val URL_WEBSITE   =   "https://mooditude.app/"
const val URL_FACEBOOK  =   "https://www.facebook.com/mooditudeapp/"
const val URL_INSTAGRAM =   "https://www.instagram.com/mooditudeapp/"
const val URL_PRIVACY =   "https://mooditude.app/privacy"
const val URL_TERMS =   "https://mooditude.app/terms"
const val EMAIL_SUPPORT_TEAM    =   "hello@mooditude.app"