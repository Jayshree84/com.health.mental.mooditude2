package com.health.mental.mooditude.data.model

import com.health.mental.mooditude.data.SharedPreferenceManager

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class M3QuestionData (var questions : ArrayList<M3Question>) {


    fun save() {
        SharedPreferenceManager.setQuestionsData(this)
    }

    companion object {

        private val listDefaultQuestionText = arrayListOf<String>(
            "I feel sad, down in the dumps or unhappy",
            "I can't concentrate or focus",
            "Nothing seems to give me much pleasure",
            "I feel tired; have no energy",
            "I have had thoughts of suicide",
            "I have difficulty sleeping",
            "I sleep too much",
            "I have lost some appetite",
            "I am eating more",
            "I feel tense, anxious or can't sit still",
            "I feel worried or fearful",
            "I have attacks of anxiety or panic",
            "I worry about dying or losing control",
            "I am nervous or shaky in social situations",
            "I have nightmares or flashbacks",
            "I am jumpy or feel startled easily",
            "I avoid places that remind of a bad experience",
            "I feel dull, numb, or detached",
            "I can't get certain thoughts out of my mind",
            "I feel I must repeat certain acts or rituals",
            "I feel the need to check and recheck things",
            "I have more energy than usual",
            "I have felt unusually irritable or angry",
            "I have felt unusually excited, revved up or high",
            "I have needed less sleep than usual",
            "Any of the previously reported symptoms interfere with work or school?",
            "Any of the previously reported symptoms affect my relationships with friends or family?",
            "Any of the previously reported symptoms has led to my using alcohol to get by? ",
            "Any of the previously reported symptoms has led to my using other substances? "
            )

        private val listDefaultQuestionShortText = arrayListOf<String>(
            "Feel sad/unhappy",
            "Can't concentrate/focus",
            "Nothing gives pleasure",
            "Tired, no energy",
            "Thoughts of suicides",
            "Sleeping too much",
            "Not sleeping enough",
            "Increased appetite",
            "Lost appetite",
            "Tense/anxious/can't sit",
            "Worried or fearful",
            "Panic Attacks",
            "Worried about dying/losing control",
            "Nervous in social situations",
            "Nightmares, flashbacks",
            "Jumpy, startled easily",
            "Avoid places",
            "Dull, numb, or detached",
            "Can't get thoughts out",
            "Must repeat rituals",
            "Need to check/recheck",
            "More energy than usual",
            "Irritable angry",
            "Excited revved high",
            "Needed less sleep",
            "Impairs work/school",
            "Impairs friends/family",
            "Led to using alcohol",
            "Led to using drugs"
            )

        fun getQuestions()  :ArrayList<M3Question> {
            val data = SharedPreferenceManager.getQuestionsData()
            if(data != null) {
                return data.questions
            }
            val data1 = M3QuestionData(getDefaultQuestions())
            data1.save()

            return data1.questions
        }

        private fun getDefaultQuestions() : ArrayList<M3Question> {
            val questions = ArrayList<M3Question>()
            for (i in 0 until 29) {
                val text = listDefaultQuestionText.get(i)
                val shortDesc = listDefaultQuestionShortText.get(i)

                /*let text = NSLocalizedString("M3_ASSESSMENT_QUESTION_\(i)", tableName: "M3Assessment", bundle: Bundle.main, value: "", comment: "")
                let shortDesc = NSLocalizedString("M3_ASSESSMENT_QUESTION_SHORT_DESC_\(i)", tableName: "M3Assessment", bundle: Bundle.main, value: "", comment: "")*/


                val quest = M3Question(i,text, shortDesc, arrayListOf(M3Question.M3AnswerChoice(0,"Not at all"),
                M3Question.M3AnswerChoice(1,"Rarely"), M3Question.M3AnswerChoice(2,"Sometimes"),
                    M3Question.M3AnswerChoice(3,"Often"), M3Question.M3AnswerChoice(4,"Most of the time")))
                questions.add(quest)

            }
            return questions
        }

        fun processData(map: ArrayList<HashMap<String,*>>) {
            val list = ArrayList<M3Question>()
            for(mapItem in map) {

                if(mapItem.get("text") != null &&
                    mapItem.get("shortText") != null &&
                    mapItem.get("position") != null &&
                    mapItem.get("choices") != null) {
                    val position: Int = (mapItem.get("position") as Long).toInt()
                    val text = mapItem.get("text").toString()
                    val shortText = mapItem.get("shortText").toString()
                    val choices = mapItem.get("choices").toString()

                    val choiceList = ArrayList<M3Question.M3AnswerChoice>()
                    val sepList = choices.split(",")
                    var idCount = 0
                    for(item in sepList) {
                        choiceList.add(M3Question.M3AnswerChoice(idCount, item))
                        idCount++
                    }

                    list.add(M3Question(position, text, shortText, choiceList))
                }
            }

            //create list
            val listOfData = M3QuestionData(list)
            listOfData.save()

        }
    }
}

