package com.health.mental.mooditude.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import com.health.mental.mooditude.debugLog
import java.util.*
import kotlin.collections.LinkedHashMap


/**
 * Created by Jayshree Rathod on 09,July,2021
 */
//fun simpleSHA1String(string: String) = SimpleCrypto.simpleSHA1(string.lowercase())

/**
 * Validates email with using android native email pattern
 */
fun validateEmail(emailAddress: String): Boolean {
    var ret = false
    try {
        ret = android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return ret
}

fun validatePhoneNumber(phone: String): Boolean {
    var ret = false
    if (phone.length != 10) {
        return ret;
    }
    try {
        ret = android.util.Patterns.PHONE.matcher(phone).matches()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return ret
}

fun validatePromoCode(code: String): Boolean {
    var ret = false
    try {
        ret = code.length >= 4
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return ret
}

fun dateFromUTC(timeInMillis: Long): Long {
    if (timeInMillis == 0L) {
        return timeInMillis
    }
    return Date(timeInMillis + Calendar.getInstance().timeZone.getOffset(timeInMillis)).time
}

fun dateToUTC(timeInMillis: Long): Long {
    if (timeInMillis == 0L) {
        return timeInMillis
    }
    return Date(timeInMillis - Calendar.getInstance().timeZone.getOffset(timeInMillis)).time
}

/*private fun removeNullValues(userObject: User): Map<String?, Any?>? {
    val gson = GsonBuilder().create()
    return Gson().fromJson(
        gson.toJson(userObject), object : TypeToken<HashMap<String?, Any?>?>() {}.getType()
    )
}*/


/**
 * Method is used for checking network availability.
 *
 * @param context
 * @return isNetAvailable: boolean true for Internet availability, false otherwise
 */

fun isInternetAvailable(context: Context): Boolean {

    val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
    } else {
        return connectivityManager.activeNetworkInfo != null &&
                connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting
    }
}


/*fun removeAndExcludeValues(userObject: Any): Any {
    val typeToken = userObject.javaClass
    val strategy: ExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            //debugLog("", "class : " + clazz.toString() + " :::: " + (clazz == userObject::class.java))
            //return (clazz == userObject::class.java)
           return false
        }

        override fun shouldSkipField(field: FieldAttributes): Boolean {
            try {
                *//*println("userObject.javaClass :: " + userObject.javaClass)
               // println("thisClass :: " + field.declaredClass)
                println("thisClass :: " + field.declaringClass)
                println("field.name :: " + field.name)
                println("field :: " + thisClass!!.getDeclaredField(field.name))
                val field1 = thisClass!!.getDeclaredField(field.name)
                field1.isAccessible = true
                println("value :: " + field1[userObject])
                val value = field1[userObject]
                debugLog("", "value : " + value)*//*

                val fieldClass = userObject.javaClass
                val orgField = fieldClass.getDeclaredField(field.name)
                orgField.isAccessible = true
                val fieldValue = orgField[userObject]
                debugLog("", "fieldValue : " + fieldValue)
                if(fieldValue == null) {
                    debugLog("TAG", "Returning true for field : " + field.name)
                    println("Returning true for field : " + field.name)
                    return true
                }
            }
            catch (e:Exception) {
                //println(e.localizedMessage)
            }
            return field.getAnnotation(Exclude::class.java) != null
        }
    }

    *//*val gson1 = GsonBuilder().addDeserializationExclusionStrategy(strategy).create()
    val textVal = gson1.toJson(userObject)
    println("textVal: " + textVal)*//*
    val gson2 = GsonBuilder().addSerializationExclusionStrategy(strategy).create()
    val gson1 = GsonBuilder()
        .setExclusionStrategies(strategy).create()
    val type = object : TypeToken<ApiEntry>() {}.type
    //return gson1.fromJson(map)
    //return gson1.fromJson(gson1.toJson(userObject),HashMap::class.java)
    //return gson1.toJsonTree(textVal).getAsJsonObject()

    //var map: Map<Any?, Any?> = HashMap<Any?, Any?>()
    //map = gson1.fromJson(InputStreamReader(inputStream), map.javaClass)
    return removeAllNullFields(userObject)
}*/

fun removeAllNullFields(classObject: Any): LinkedHashMap<Any, Any> {
    //To be delete
    val map = LinkedHashMap<Any, Any>()
    for (field in classObject.javaClass.declaredFields) {
        field.isAccessible = true // You might want to set modifier to public first.
        val value = field[classObject]
        println("field : " + field.genericType)
        println("field value: " + value)

        if (value != null) {
            println(field.name.toString() + "=" + value)
            map.put(field.name, field[classObject])
        }
    }
    return map
}

//fun getUTCDate() = Date(CalendarUtils.getUTCTime())

fun openURL(context: Context, url: String?) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun getUserCountry(context: Context): String? {
    try {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        if (simCountry != null && simCountry.length == 2) { // SIM country code is available
            return simCountry.lowercase(Locale.US)
        } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // Device is not 3G (would be unreliable)
            val networkCountry = tm.networkCountryIso
            if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                return networkCountry.lowercase(Locale.US)
            }
        }
    } catch (e: Exception) {
    }
    return null
}

fun isCountrySupported(context: Context) : Boolean {
    val userCountry = getUserCountry(context)
    debugLog("Utils", "User country : " + userCountry)

    if(userCountry != null && userCountry.equals("us")) {
        return true
    }
    return false
}

fun getRandomNumber(start: Int, end: Int): Int {
    require(start <= end) { "Illegal Argument" }
    return (start..end).random()
}

fun getStringFromName(context: Context, stringName:String): String {
    val id = context.getResources().getIdentifier(stringName, "string", context.packageName);
    return context.getString(id)
}
