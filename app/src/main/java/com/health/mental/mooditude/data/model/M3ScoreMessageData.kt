package com.health.mental.mooditude.data.model

import com.health.mental.mooditude.data.SharedPreferenceManager

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class M3ScoreMessageData ( val messages:ArrayList<M3ScoreMessage>) {

    fun save() {
        SharedPreferenceManager.setM3ScoreMessageData(this)
    }

    companion object  {
        fun getAllMessages() : ArrayList<M3ScoreMessage> {
            val data = SharedPreferenceManager.getM3ScoreMessageData()
            if(data != null) {
                return data.messages
            }
            val data1 = M3ScoreMessageData(defaultMessages())
            data1.save()

            return data1.messages
        }

        fun defaultMessages() : ArrayList<M3ScoreMessage> {
            val questions = ArrayList<M3ScoreMessage>()
            for (i in 1.. 29) {
                val text = "text"+i
                val shortDesc = "text" + i

                /*let text = NSLocalizedString("M3_ASSESSMENT_QUESTION_\(i)", tableName: "M3Assessment", bundle: Bundle.main, value: "", comment: "")
                let shortDesc = NSLocalizedString("M3_ASSESSMENT_QUESTION_SHORT_DESC_\(i)", tableName: "M3Assessment", bundle: Bundle.main, value: "", comment: "")*/


                val quest = M3ScoreMessage()

                questions.add(quest)

            }
            return questions
        }

        fun processData(map: ArrayList<HashMap<String,*>>) {
            val list = ArrayList<M3ScoreMessage>()
            for(mapItem in map) {

                if(mapItem.get("disorder") != null &&
                    mapItem.get("intensity") != null &&
                    mapItem.get("message") != null ) {
                    val disorder: String = mapItem.get("disorder").toString()
                    val intensity:Int = (mapItem.get("intensity") as Long).toInt()
                    val message = mapItem.get("message").toString()

                    list.add(M3ScoreMessage(disorder.lowercase(), intensity, message))
                }
            }

            //create list
            val listOfData = M3ScoreMessageData(list)
            listOfData.save()
        }
    }

    class M3ScoreMessage (
        var disorder: String = "",
        var intensity: Int = 0,
        var message: String = "",
        var score: Int = 0)
    {
        fun getDisorderIntensity() : M3DisorderIntensity {
            var type:M3DisorderIntensity = M3DisorderIntensity.unlikely
            when(intensity) {
                1-> {
                    type = M3DisorderIntensity.low
                }
                2 -> {
                    type = M3DisorderIntensity.medium
                }
                3 -> {
                    type = M3DisorderIntensity.high
                }
            }
            return type
        }

        fun therapistSuggestionMsg():String = "message"
    }




        /*var therapistSuggestionMsg: String {
            if intensity == 1 {
                return R.string.m3Assessment.m3_ASSESSMENT_THERAPIST_SUGGESSION_ALL_2()
            } else if intensity == 2 {
                return R.string.m3Assessment.m3_ASSESSMENT_THERAPIST_SUGGESSION_ALL_3()
            } else if intensity == 3 {
                return R.string.m3Assessment.m3_ASSESSMENT_THERAPIST_SUGGESSION_ALL_4()
            } else {
                return R.string.m3Assessment.m3_ASSESSMENT_THERAPIST_SUGGESSION_ALL_1()
            }
        }*/

        //var disorderType: M3DisorderType? = M3DisorderType()



    }