package com.health.mental.mooditude.core

import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.data.model.M3Question
import com.health.mental.mooditude.data.model.M3QuestionData
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.model.M3AssessmentIntensity
import com.health.mental.mooditude.utils.ASSESSMENT_EXPIRY_DAYS
import com.health.mental.mooditude.utils.CalendarUtils
import java.util.*

/**
 * Created by Jayshree Rathod on 29,July,2021
 */
class M3AssessmentManager {

    //used for logging purpose
    protected val TAG = this.javaClass.simpleName

    private var questions: java.util.ArrayList<M3Question>
    init {
        questions = M3QuestionData.getQuestions()
        questions.sortBy { it.position }
    }

    private object Holder {
        val INSTANCE = M3AssessmentManager()
    }

    /**
     * This function reads questions from database with selectedoption blank
     * So for any new assessment user has to select option for each question
     */
    fun getQuestionsForAssessment(): ArrayList<M3Question> {
        questions = M3QuestionData.getQuestions()
        questions.sortBy { it.position }

        return this.questions
    }

    /**
     * This returns the questions already owned by assessment start activity
     * It holds all information with user selection and timetoanswer, so it can be used
     * to calculate score
     */
    fun getQuestions() = this.questions
    fun getQuestion(index: Int): M3Question? {

        if (index < questions.size) {
            return questions[index]
        }
        return null
    }

    companion object {

        private val DEPRESSION_MAX_SCORE: Int = 14
        private val ANXIETY_MAX_SCORE: Int = 24
        private val PTSD_MAX_SCORE: Int = 8
        private val BIPOLAR_MAX_SCORE: Int = 8
        private val ALLSCORE_MAX_SCORE: Int = 108
        private val TAG = this.javaClass.simpleName

        val instance: M3AssessmentManager by lazy { Holder.INSTANCE }

        fun getIntensityForAllScore(score: Int): M3AssessmentIntensity {
            var riskFactor = M3AssessmentIntensity.unlikely
            if (score <= 1) {
                // unlikly
                riskFactor = M3AssessmentIntensity.unlikely

            } else if (score >= 2 && score <= 32) {
                riskFactor = M3AssessmentIntensity.low

            } else if (score >= 33 && score <= 50) {
                riskFactor = M3AssessmentIntensity.medium

            } else if (score >= 51 && score <= 108) {
                riskFactor = M3AssessmentIntensity.high
            }

            return riskFactor
        }

        fun getScoreBgColorID(intensity: M3AssessmentIntensity): Int {

            var color = R.color.risk_unlikely
            when (intensity) {
                M3AssessmentIntensity.unlikely -> {
                    color = R.color.risk_unlikely
                }
                M3AssessmentIntensity.low -> {
                    color = R.color.risk_low
                }
                M3AssessmentIntensity.medium -> {
                    color = R.color.risk_medium
                }
                M3AssessmentIntensity.high -> {
                    color = R.color.risk_high
                }
            }

            return color
        }

        fun getRiskTextID(intensity: M3AssessmentIntensity): Int {

            var textID = R.string.unlikely_risk
            when (intensity) {
                M3AssessmentIntensity.unlikely -> {
                    textID = R.string.unlikely_risk
                }
                M3AssessmentIntensity.low -> {
                    textID = R.string.low_risk
                }
                M3AssessmentIntensity.medium -> {
                    textID = R.string.medium_risk
                }
                M3AssessmentIntensity.high -> {
                    textID = R.string.high_risk
                }
            }

            return textID
        }

        fun getRiskDescTextID(intensity: M3AssessmentIntensity): Int {

            var textID = 0
            when (intensity) {
                M3AssessmentIntensity.unlikely -> {
                    textID = R.string.unlikely_risk_desc
                }
                M3AssessmentIntensity.low -> {
                    textID = R.string.low_risk_desc
                }
                M3AssessmentIntensity.medium -> {
                    textID = R.string.medium_risk_desc
                }
                M3AssessmentIntensity.high -> {
                    textID = R.string.high_risk_desc
                }
            }

            return textID
        }

        fun getScoreDetailedTextID(intensity: M3AssessmentIntensity): Int {

            var textID = 0
            when (intensity) {
                M3AssessmentIntensity.unlikely -> {
                    textID = R.string.unlikely_risk_details
                }
                M3AssessmentIntensity.low -> {
                    textID = R.string.low_risk_details
                }
                M3AssessmentIntensity.medium -> {
                    textID = R.string.medium_risk_details
                }
                M3AssessmentIntensity.high -> {
                    textID = R.string.high_risk_details
                }
            }

            return textID
        }

        fun getDepressionMaxScore() = DEPRESSION_MAX_SCORE
        fun getAnxietyMaxScore() = ANXIETY_MAX_SCORE
        fun getPTSDMaxScore() = PTSD_MAX_SCORE
        fun getBipolarMaxScore() = BIPOLAR_MAX_SCORE

        fun getDepressionIntensity(score: Int): M3AssessmentIntensity {
            var intensity: M3AssessmentIntensity = M3AssessmentIntensity.unlikely
            if (score <= 4) {
                // unlikly
                intensity = M3AssessmentIntensity.unlikely

            } else if (score >= 5 && score <= 7) {
                intensity = M3AssessmentIntensity.low

            } else if (score >= 8 && score <= 10) {
                intensity = M3AssessmentIntensity.medium

            } else if (score >= 11 && score <= 14) {
                intensity = M3AssessmentIntensity.high
            }
            return intensity
        }

        fun getDepressionMessageID(score: Int): Int {

            val intensity = getDepressionIntensity(score)

            when (intensity) {
                M3AssessmentIntensity.unlikely -> return R.string.depression_msg_unlikely
                M3AssessmentIntensity.low -> return R.string.depression_msg_low
                M3AssessmentIntensity.medium -> return R.string.depression_msg_medium
                M3AssessmentIntensity.high -> return R.string.depression_msg_high
            }
        }

        fun getAnxietyIntensity(score: Int): M3AssessmentIntensity {
            var intensity: M3AssessmentIntensity = M3AssessmentIntensity.unlikely
            if (score <= 2) {
                // unlikly
                intensity = M3AssessmentIntensity.unlikely

            } else if (score >= 3 && score <= 5) {
                intensity = M3AssessmentIntensity.low

            } else if (score >= 6 && score <= 11) {
                intensity = M3AssessmentIntensity.medium

            } else if (score >= 12 && score <= 24) {
                intensity = M3AssessmentIntensity.high
            }
            return intensity
        }

        fun getAnxietyMessageID(score: Int): Int {

            val intensity = getAnxietyIntensity(score)

            when (intensity) {
                M3AssessmentIntensity.unlikely -> return R.string.anxiety_msg_unlikely
                M3AssessmentIntensity.low -> return R.string.anxiety_msg_low
                M3AssessmentIntensity.medium -> return R.string.anxiety_msg_medium
                M3AssessmentIntensity.high -> return R.string.anxiety_msg_high
            }
        }

        fun getPTSDIntensity(score: Int): M3AssessmentIntensity {
            var intensity: M3AssessmentIntensity = M3AssessmentIntensity.unlikely
            if (score <= 1) {
                // unlikly
                intensity = M3AssessmentIntensity.unlikely

            } else if (score >= 2 && score <= 3) {
                intensity = M3AssessmentIntensity.low

            } else if (score >= 4 && score <= 5) {
                intensity = M3AssessmentIntensity.medium

            } else if (score >= 6 && score <= 8) {
                intensity = M3AssessmentIntensity.high
            }
            return intensity
        }

        fun getPTSDMessageID(score: Int): Int {
            val intensity = getPTSDIntensity(score)

            when (intensity) {
                M3AssessmentIntensity.unlikely -> return R.string.ptsd_msg_unlikely
                M3AssessmentIntensity.low -> return R.string.ptsd_msg_low
                M3AssessmentIntensity.medium -> return R.string.ptsd_msg_medium
                M3AssessmentIntensity.high -> return R.string.ptsd_msg_high
            }
        }

        fun getBipolarIntensity(score: Int): M3AssessmentIntensity {
            var intensity: M3AssessmentIntensity = M3AssessmentIntensity.unlikely
            if (score <= 1) {
                // unlikly
                intensity = M3AssessmentIntensity.unlikely

            } else if (score >= 2 && score <= 3) {
                intensity = M3AssessmentIntensity.low

            } else if (score >= 4 && score <= 6) {
                intensity = M3AssessmentIntensity.medium

            } else if (score >= 7 && score <= 8) {
                intensity = M3AssessmentIntensity.high
            }
            return intensity
        }

        fun getBipolarMessageID(score: Int): Int {

            val intensity = getBipolarIntensity(score)

            when (intensity) {
                M3AssessmentIntensity.unlikely -> return R.string.bipolar_msg_unlikely
                M3AssessmentIntensity.low -> return R.string.bipolar_msg_low
                M3AssessmentIntensity.medium -> return R.string.bipolar_msg_medium
                M3AssessmentIntensity.high -> return R.string.bipolar_msg_high
            }
        }

        fun getRecommendedActionTextID(intensity: M3AssessmentIntensity): Int {
            var textID = 0
            when (intensity) {
                M3AssessmentIntensity.unlikely -> {
                    textID = R.string.recommended_action_unlikely
                }
                M3AssessmentIntensity.low -> {
                    textID = R.string.recommended_action_low
                }
                M3AssessmentIntensity.medium -> {
                    textID = R.string.recommended_action_medium
                }
                M3AssessmentIntensity.high -> {
                    textID = R.string.recommended_action_high
                }
            }

            return textID
        }

        fun hasSuicidalThoughts(selectedOption5: Int?): Boolean {

            //get 5th question

            if (selectedOption5 == null) {
                return false
            }
            val answerId = selectedOption5
            return (answerId > 0)
        }

        fun fetchActionsForBlock(selectedOption28: Int, selectedOption29: Int): Int {
            //no block
            var action = 0
            //last two question
            //28 question is for alcohol
            //29 question is for drug
            if (selectedOption28 > 0) {
                //only alcochol
                action = 1
            }

            if (selectedOption29 > 0) {

                //check if having alcohol already
                if (action == 1) {
                    //both - alcohol n drug
                    action = 3
                } else {
                    //only drug
                    action = 2
                }
            }
            return action
        }

        fun saveAssessment(m3AssessmentRecord: M3Assessment) {

            //Save on firestore
            DBManager.instance.saveM3AssessmentData(m3AssessmentRecord)
        }


        fun makeCurrentAssessment(): M3Assessment {
            //get questions
            val questions = instance.getQuestions()

            //calculate score
            var allScore: Int = 0
            var gatewayScore: Int = 0
            var depressionScore: Int = 0
            var gadScore: Int = 0
            var panicScore: Int = 0
            var socialAnxietyScore: Int = 0
            var ptsdScore: Int = 0
            var ocdScore: Int = 0
            var bipolarScore: Int = 0
            //var pdfDocUrl: String? = null

            var anxietyScore = 0
            var overallScore = 0

            for (question in questions) {
                debugLog(TAG, "Question : " + question.position + " : " + question.selectedOption)

                if (question.selectedOption == null) {
                    debugLog(TAG, "Ignoring question : " + question.shortText)
                    continue
                }

                if (question.position == 7 || question.position == 9) {
                    debugLog(TAG, "Ignoring question : " + question.shortText)
                    continue
                }

                var selectedValue = question.selectedOption!!
                // overall Score
                if (question.position == 6) {
                    //Chec for seventh question , it's at 6th postion
                    val nextVaulue = questions[6].selectedOption ?: 0
                    selectedValue = if (nextVaulue > selectedValue) nextVaulue else selectedValue
                } else if (question.position == 8) {
                    val nextVaulue = questions[8].selectedOption ?: 0
                    selectedValue = if (nextVaulue > selectedValue) nextVaulue else selectedValue
                }

                allScore += selectedValue

                val scaledValue = getRiskScoringValue(selectedValue)
                if (question.position <= 9) {
                    depressionScore += scaledValue
                }

                if (question.position >= 10 && question.position <= 11) {
                    gadScore += scaledValue
                }

                if (question.position >= 12 && question.position <= 13) {
                    panicScore += scaledValue
                }

                if (question.position == 14) {
                    socialAnxietyScore += scaledValue
                }

                if (question.position >= 15 && question.position <= 18) {
                    ptsdScore += scaledValue
                }

                if (question.position >= 19 && question.position <= 21) {
                    ocdScore += scaledValue
                }

                if (question.position >= 22 && question.position <= 25) {
                    bipolarScore += scaledValue
                }

                if (question.position == 5 || question.position > 25) {
                    gatewayScore += getGatewayScoringValue(question)
                }
            }

            anxietyScore = gadScore + panicScore + socialAnxietyScore + ptsdScore + ocdScore
            //Not in use
            overallScore = allScore + gatewayScore

            //print log
            debugLog(TAG,
                " Score :  allScore : " + allScore +
                        "\ngatewayScore : " + gatewayScore +
                        "\ndepressionScore : " + depressionScore +
                        "\ngadScore : " + gadScore +
                        "\npanicScore : " + panicScore +
                        "\nsocialAnxietyScore : " + socialAnxietyScore +
                        "\nanxietyScore : " + anxietyScore +
                        "\nptsdScore : " + ptsdScore +
                        "\nocdScore : " + ocdScore +
                        "\nbipolarScore : " + bipolarScore +
                        "\noverallScore : " + overallScore
            )

            //comma seperated string of question's selections
            var rawData = ""
            var rawTimeToAnswer = ""
            for (question in questions) {
                if (question.selectedOption != null) {
                    rawData += question.selectedOption!!.toString() + ","
                    rawTimeToAnswer += question.timeToAnswerInSeconds.toString() + ","
                }
            }
            rawData = rawData.trim(',')
            rawTimeToAnswer = rawTimeToAnswer.trim(',')

            val id = CalendarUtils.getStartTimeOfDay().toString()
            val createDate: Date = Date(System.currentTimeMillis())
            //create M3Assessment record and save it to database
            val m3AssessmentRecord = M3Assessment(id)
            m3AssessmentRecord.init(
                createDate,
                rawData,
                rawTimeToAnswer,
                allScore,
                gatewayScore,
                depressionScore,
                gadScore,
                panicScore,
                socialAnxietyScore,
                ptsdScore,
                ocdScore,
                bipolarScore
            )

            return m3AssessmentRecord
        }

        private fun getRiskScoringValue(value: Int): Int {
            if (value == 0 || value == 1) {
                return 0
            }
            if (value == 2) {
                return 1
            }
            if (value == 3) {
                return 2
            }
            if (value == 4) {
                return 2
            }

            return 0
        }

        private fun getGatewayScoringValue(question: M3Question): Int {
            if (question.selectedOption == null) {
                return 0
            }
            val value = question.selectedOption!!

            if (question.position == 5 && value >= 1) {
                return 1
            }

            if (question.position >= 26 && question.position < 29 && value >= 3) {
                return 3
            }

            if (question.position == 29 && value >= 1) {
                return 1
            }
            return 0
        }

        fun getPercentageForDepression(depressionScore: Int): Float {
            debugLog(TAG, "Depression Mx score : " + DEPRESSION_MAX_SCORE + " : " + depressionScore)
            val percentage = (depressionScore.toFloat() / DEPRESSION_MAX_SCORE)*100
            debugLog(TAG, "Depression percentage : " + percentage)
            return percentage
        }

        fun getPercentageForAnxiety(anxietyScore: Int): Float {
            debugLog(TAG, "Anxiety Mx score : " + ANXIETY_MAX_SCORE + " : " + anxietyScore)
            val percentage = (anxietyScore.toFloat() / ANXIETY_MAX_SCORE)*100
            debugLog(TAG, "Anxiety percentage : " + percentage)
            return percentage
        }

        fun getPercentageForPTSD(ptsdScore: Int): Float {
            debugLog(TAG, "PTSD Mx score : " + PTSD_MAX_SCORE + " : " + ptsdScore)
            val percentage = (ptsdScore.toFloat() / PTSD_MAX_SCORE)*100
            debugLog(TAG, "PTSD percentage : " + percentage)
            return percentage
        }

        fun getPercentageForBipolar(bipolarScore: Int): Float {
            debugLog(TAG, "Bipolar Mx score : " + BIPOLAR_MAX_SCORE + " : " + bipolarScore)
            val percentage = (bipolarScore.toFloat() / BIPOLAR_MAX_SCORE)*100
            debugLog(TAG, "Depression percentage : " + percentage)
            return percentage
        }

        fun getPercentageForAllScore(score: Int): Float {
            debugLog(TAG, "ALL score : " + ALLSCORE_MAX_SCORE + " : " + score)
            val percentage = (score.toFloat() / ALLSCORE_MAX_SCORE)*100
            debugLog(TAG, "All score percentage : " + percentage)
            return percentage
        }

        fun isAssessmentExpired(assessment: M3Assessment): Boolean {
            val diffInDays = CalendarUtils.getDiffInDays(
                assessment.createDate,
                Date(System.currentTimeMillis())
            )

            debugLog(TAG, "Difference in days : " + diffInDays)
            return (diffInDays >= ASSESSMENT_EXPIRY_DAYS)
        }

    }
}