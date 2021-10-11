package com.health.mental.mooditude.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.databinding.ActivityM3AssessmentScoreBinding

class M3AssessmentScoreActivity : BaseActivity() {

    private lateinit var binding: ActivityM3AssessmentScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityM3AssessmentScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        setPageTitle(findViewById(R.id.toolbar), getString(R.string.your_score))

        val mM3Assessment = M3AssessmentManager.makeCurrentAssessment()
        val allScore = mM3Assessment.allScore

        val intensity = M3AssessmentManager.getIntensityForAllScore(allScore)

        setupAssessmentTopbar(mM3Assessment, binding.assessmentTopbar)

        val detailText = String.format(getString(M3AssessmentManager.getScoreDetailedTextID(intensity)), allScore)

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvScoreDetails.setText(Html.fromHtml(detailText, 0))
        } else {
            binding.tvScoreDetails.setText(Html.fromHtml(detailText))
        }

        binding.btnNoThanks.setOnClickListener {
            var showHome = true
            val extras = intent.extras
            if(extras != null) {
                showHome = extras.getBoolean("show_home", true)
            }

            if(showHome) {
                showHomePage(HomeActivity.ScreenMode.Tracking)
            }
            else {
                //Just finish it
                finish()
            }
        }
        binding.btnViewFull.setOnClickListener {
            //showAssessmentScoreDetailedPage()
            showHomePage(HomeActivity.ScreenMode.Tracking)
        }

        //Save this assessment data on firebase
        M3AssessmentManager.saveAssessment(mM3Assessment)
        SharedPreferenceManager.setAssessmentCompleted(true)
    }


}