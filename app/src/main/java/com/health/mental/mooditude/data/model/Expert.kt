package com.health.mental.mooditude.data.model

import com.google.gson.Gson
import java.net.URL

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
data class Expert (
    val id: String = "",
    val name: String = "",
    var imgStr: String? = null,
    var qualifications: String? = null,
    var bio: String? = null)
{
    private var contact:ArrayList<ContactInfo>? = null
    private val imageUrl:URL? = null

    companion object {
        //Fetch expert object
        fun getExpertData(expertStr : String?): Expert? {
            var expert:Expert? = null
            if(expertStr != null) {
                val json = Gson().toJson(expertStr)
                expert =  Gson().fromJson(json, Expert::class.java)
            }
            return expert
        }
    }
    fun getUrlForImage() : URL?{
        if(imgStr != null) {
            return URL(imgStr)
        }
        return null
    }

    private final class ContactInfo {
        var type: String = ""
        var value: String = ""

        var image:Int = 0
        /*var image: UIImage? {
            if type == "website" {
                return R.image.website_logo()
            }

            if type == "facebook" {
                return R.image.facebook()
            }

            if type == "twitter" {
                return R.image.twitter()
            }

            if type == "instagram"{
                return R.image.instagram()
            }

            return R.image.website()
        }*/
    }

}





