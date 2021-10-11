package com.health.mental.mooditude.utils

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.health.mental.mooditude.warnLog
import java.io.IOException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jayshree Rathod on 30,August,2021
 */
class TypeAdapters {
    internal class UnixEpochDateTypeAdapter private constructor() : TypeAdapter<Date>() {
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): Date {
            // this is where the conversion is performed
            return Date(`in`.nextString())
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Date) {
            // write back if necessary or throw UnsupportedOperationException
            out.value(value.toString())
        }

        companion object {
            val unixEpochDateTypeAdapter: TypeAdapter<Date> = UnixEpochDateTypeAdapter()
        }
    }

    internal class DateDeserializer : JsonDeserializer<Date?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Date {
            try {
                return Gson().fromJson(json, Date::class.java)
            } catch (e: JsonSyntaxException) {
            }
            val timeString = json.asString
            warnLog("TAG", "Standard date deserialization didn't work:$timeString")
            try {
                return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(timeString)
            } catch (e: ParseException) {
            }
            warnLog("TAG","Parsing as json 24 didn't work:$timeString")
            return Date(json.asLong)
        }
    }
}