package com.health.mental.mooditude.data.model

import java.io.Serializable

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class M3Question() : Serializable {
    var position: Int = 0
    var options = ArrayList<M3AnswerChoice>()
    var text: String = ""
    var shortText: String = ""
    var selectedOption: Int? = null
    var timeToAnswerInSeconds:Int = 0

    /*constructor(parcel: Parcel) : this() {
        position = parcel.readInt()
        text = parcel.readString().toString()
        shortText = parcel.readString().toString()
        selectedOption = parcel.readValue(Int::class.java.classLoader) as? Int
        options = parcel.
    }*/

    constructor(id:Int, text:String, shortText:String, choices:ArrayList<M3AnswerChoice>) : this() {
        this.position = id
        this.text = text
        this.shortText = shortText
        this.options = choices
        this.timeToAnswerInSeconds = 0
    }
    fun getSelectedAnswer(): M3AnswerChoice? {
        if (selectedOption == null) return null
        return options[selectedOption!!]
    }


    //MARK: AnswerChoice
    class M3AnswerChoice(var id: Int = 0,
                         var text: String = ""):Serializable {



        /*var color: UIColor {
            return M3AnswerChoice.getColor(for: id)
        }*/
        var color = 0

        companion object {
            fun getColor(choice: Int): Int {
                val uiColor = 0
                /*when (choice) {
                    0 -> {

                }*/
                return uiColor
            }

        }


    }

   /* override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(position)
        dest.writeString(text)
        dest.writeString(shortText)
        selectedOption?.let { dest.writeInt(it) }
    }

    companion object CREATOR : Parcelable.Creator<M3Question> {
        override fun createFromParcel(parcel: Parcel): M3Question {
            return M3Question(parcel)
        }

        override fun newArray(size: Int): Array<M3Question?> {
            return arrayOfNulls(size)
        }
    }*/


}