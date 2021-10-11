package com.health.mental.mooditude.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.*
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.health.mental.mooditude.data.entity.*
import com.health.mental.mooditude.utils.*
import com.mindorks.example.coroutines.data.local.DatabaseBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


private val TRUE_STRINGS: Array<String> = arrayOf("true", "1")

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    class BooleanObjectTypeAdapter : JsonDeserializer<Boolean?>, JsonSerializer<Boolean?> {

        override fun serialize(src: Boolean?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return if (src == null) {
                JsonNull.INSTANCE
            } else {
                JsonPrimitive(src)
            }
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Boolean? {
            if (json == null || json.isJsonNull) {
                return null
            }

            return when {
                json !is JsonPrimitive -> false
                json.isBoolean -> json.asBoolean
                json.isNumber -> json.asNumber.toInt() == 1
                json.isString -> TRUE_STRINGS.contains(json.asString.lowercase())
                else -> false
            }
        }
    }

    class BooleanPrimitiveTypeAdapter : JsonDeserializer<Boolean>, JsonSerializer<Boolean> {

        override fun serialize(src: Boolean?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return if (src == null) {
                JsonPrimitive(false)
            } else {
                JsonPrimitive(src)
            }
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Boolean {
            if (json == null || json.isJsonNull) {
                return false
            }

            return when {
                json !is JsonPrimitive -> false
                json.isBoolean -> json.asBoolean
                json.isNumber -> json.asNumber.toInt() == 1
                json.isString -> TRUE_STRINGS.contains(json.asString.lowercase())
                else -> false
            }
        }
    }

    class CostTypeAdapter : JsonDeserializer<Double>, JsonSerializer<Double?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Double {
            val cost: Double
            cost = try {
                json.asDouble
            } catch (e: NumberFormatException) {
                0.00
            }
            return cost
        }

        override fun serialize(
            src: Double?, typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(src)
        }
    }

    class CostTypeAdapterForLong : JsonDeserializer<Long>, JsonSerializer<Long?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Long {
            val cost: Long
            cost = try {
                json.asLong
            } catch (e: NumberFormatException) {
                0L
            }
            return cost
        }

        override fun serialize(
            src: Long?, typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(src)
        }
    }

    internal class UnixEpochDateTypeAdapter private constructor() : TypeAdapter<Date>() {
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): Date {
            // this is where the conversion is performed
            return Date(`in`.nextLong())
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Date) {
            // write back if necessary or throw UnsupportedOperationException
            out.value(value.getTime())
        }

        companion object {
            val unixEpochDateTypeAdapter: TypeAdapter<Date> = UnixEpochDateTypeAdapter()
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            //Dump articles
            val filename = inputData.getString(KEY_DATA_FILENAME)
            if (filename != null) {
                applicationContext.assets.open(filename).use { inputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader ->

                        val gsonBuilder = GsonBuilder()
                            .registerTypeAdapter(Boolean::class.javaObjectType, BooleanObjectTypeAdapter())
                            .registerTypeAdapter(Boolean::class.javaPrimitiveType, BooleanPrimitiveTypeAdapter())
                            .registerTypeAdapter(Date::class.javaObjectType, UnixEpochDateTypeAdapter.unixEpochDateTypeAdapter)
                            .registerTypeAdapter(Double::class.javaObjectType, CostTypeAdapter())
                            .create()

                        /*if(filename.equals(ARTICLE_DATA_FILENAME)) {
                            val articleType = object : TypeToken<List<Article>>() {}.type
                            val articalList: List<Article> =
                                gsonBuilder.fromJson(jsonReader, articleType)

                            DatabaseBuilder.getInstance(applicationContext).articleDao().insertAll(articalList)
                            println("TABLE CREATED : Articles")
                        }
                        else if(filename.equals(MEDITATION_CATEGORY_FILENAME)) {
                            val meditationCategoryType =
                                object : TypeToken<List<MeditationCategory>>() {}.type
                            val meditationCategoryList: List<MeditationCategory> =
                                gsonBuilder.fromJson(jsonReader, meditationCategoryType)

                            DatabaseBuilder.getInstance(applicationContext).meditationCategoryDao()
                                .insertAll(meditationCategoryList)
                            println("TABLE CREATED : Meditation Category")
                        }
                        else if(filename.equals(USER_ACTIVITY_FILENAME)) {
                            val type = object : TypeToken<List<UserActivity>>() {}.type
                            val list: List<UserActivity> =
                                gsonBuilder.fromJson(jsonReader, type)

                            DatabaseBuilder.getInstance(applicationContext).userActivityDao().insertAll(list)
                            println("TABLE CREATED : UserActivity")
                        }
                        else if(filename.equals(PROMPT_CATEGORY_FILENAME)) {
                            val type = object : TypeToken<List<PromptCategory>>() {}.type
                            val list: List<PromptCategory> =
                                gsonBuilder.fromJson(jsonReader, type)

                            DatabaseBuilder.getInstance(applicationContext).journalPromptCatDao().insertAll(list)
                            println("TABLE CREATED : PROMPT CATEGORY")
                        }
                        else if(filename.equals(JOURNAL_PROMPT_FILENAME)) {
                            val type = object : TypeToken<List<JournalPrompt>>() {}.type
                            val list: List<JournalPrompt> =
                                gsonBuilder.fromJson(jsonReader, type)

                            DatabaseBuilder.getInstance(applicationContext).journalPromptDao().insertAll(list)
                            println("TABLE CREATED : JournalPrompt")
                        }
*/
                        Result.success()
                    }
                }
            } else {
                Log.e(TAG, "Error seeding database - no valid filename")
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
        const val KEY_DATA_FILENAME = "KEY_DATA_FILENAME"
    }
}
