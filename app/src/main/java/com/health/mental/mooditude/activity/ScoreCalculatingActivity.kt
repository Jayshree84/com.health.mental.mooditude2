package com.health.mental.mooditude.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.ActivityScoreCalculatingBinding

class ScoreCalculatingActivity : BaseActivity() {
    private lateinit var binding: ActivityScoreCalculatingBinding

    private var progressBarPercentages = DoubleArray(5)

    private var mCurrentQuestionPos = 25
    private val mTotalQuestions = 29

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreCalculatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initActionBar(findViewById(R.id.toolbar))
        initComponents()
    }

    override fun initComponents() {
        setupRandomData()

        //val question = M3Assessment.instance.getQuestion(currentQuestion)
        startTimer()
    }

    private fun startTimer() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            kotlin.run {
                //set progress
                //binding.tvProgress.text = "31"
                if (mCurrentQuestionPos < mTotalQuestions) {
                    showAssessmentFlowDialog2(mCurrentQuestionPos)
                }
                //Else show total score
                else {
                    val showHome = intent.extras!!.getBoolean("show_home")
                    showAssessmentScoreDetailedPage(showHome)
                }
            }
        }

        handler.postDelayed(runnable, 2000)
    }

    private fun random(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return (start..end).random()
    }

    private fun setupRandomData() {
        val v1: Double = random(15, 25).toDouble()
        val p1 = v1 / 100
        progressBarPercentages.set(0, p1)

        val v2: Double = random(30, 50).toDouble()
        val p2 = v2 / 100
        progressBarPercentages.set(1, p2)

        val v3: Double = random(55, 70).toDouble()
        val p3 = v3 / 100
        progressBarPercentages.set(2, p3)

        val v4: Double = random(75, 85).toDouble()
        val p4 = v4 / 100
        progressBarPercentages.set(3, p4)

        val p5: Double = 100.0
        progressBarPercentages.set(4, p5)
    }

    private fun getPercentageFor(questPos: Int): Double {
        when (questPos) {
            25 -> {
                return progressBarPercentages[0]
            }
            26 -> {
                return progressBarPercentages[1]
            }
            27 -> {
                return progressBarPercentages[2]
            }
            28 -> {
                return progressBarPercentages[3]
            }
            29 -> {
                return progressBarPercentages[4]
            }
            else -> {
                return progressBarPercentages[4]
            }
        }
    }

    fun showAssessmentNextQuestion() {
        mCurrentQuestionPos++
        startTimer()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

}