package com.health.mental.mooditude.activity.ui.tracking

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.databinding.FragmentTrackingDetailsBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.model.M3AssessmentIntensity
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.isCountrySupported


class FullReportFragment(val mM3Assessment: M3Assessment) : BaseFragment() {

    private val binding get() = _binding!! as FragmentTrackingDetailsBinding

    companion object {

        fun newInstance(param1: M3Assessment, tabPosition: Int): FullReportFragment {
            val fragment = FullReportFragment(param1)
            val bundle = Bundle()
            bundle.putInt("tab_position", tabPosition)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentTrackingDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initComponents()

        val position = requireArguments().get("tab_position")
        if (position == 1) {
            binding.tvTabScore.callOnClick()
        } else {
            binding.tvTabReport.callOnClick()
        }

        return root
    }

    fun initComponents() {
        //calculate score
        //No need to calculate again/ already calculated
        //m3Assessmet.calculateScore()
        val allScore = mM3Assessment.allScore

        val intensity = M3AssessmentManager.getIntensityForAllScore(allScore)
        (requireActivity() as BaseActivity).setupAssessmentTopbar(
            mM3Assessment,
            binding.assessmentTopbar
        )

        initReportTab(allScore, intensity)
        initScoreTab()

        initTabLayout()
    }

    private fun initReportTab(allScore: Int, intensity: M3AssessmentIntensity) {
        val detailText = String.format(
            getString(M3AssessmentManager.getScoreDetailedTextID(intensity)),
            allScore
        )

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvScoreDetails.setText(Html.fromHtml(detailText, 0))
        } else {
            binding.tabReport.tvScoreDetails.setText(Html.fromHtml(detailText))
        }

        //now for depression
        binding.tabReport.progressDepression.max = M3AssessmentManager.getDepressionMaxScore()
        binding.tabReport.progressDepression.progress = mM3Assessment.depressionScore
        binding.tabReport.tvDepressionDesc.text =
            getString(M3AssessmentManager.getDepressionMessageID(mM3Assessment.depressionScore))

        binding.tabReport.progressAnxiety.max = M3AssessmentManager.getAnxietyMaxScore()
        binding.tabReport.progressAnxiety.progress = mM3Assessment.getAnxietyScore()
        binding.tabReport.tvAnxietyDesc.text =
            getString(M3AssessmentManager.getAnxietyMessageID(mM3Assessment.getAnxietyScore()))

        binding.tabReport.progressPtsd.max = M3AssessmentManager.getPTSDMaxScore()
        binding.tabReport.progressPtsd.progress = mM3Assessment.ptsdScore
        binding.tabReport.tvPtsdDesc.text =
            getString(M3AssessmentManager.getPTSDMessageID(mM3Assessment.ptsdScore))

        binding.tabReport.progressBipolar.max = M3AssessmentManager.getBipolarMaxScore()
        binding.tabReport.progressBipolar.progress = mM3Assessment.bipolarScore
        binding.tabReport.tvBipolarDesc.text =
            getString(M3AssessmentManager.getBipolarMessageID(mM3Assessment.bipolarScore))

        binding.tabReport.btnFindTherapist.setOnClickListener {
            (requireActivity() as BaseActivity).onNeedTherapistBtnClicked()
        }

        if (allScore > 1) {
            binding.tabReport.btnFindTherapist.visibility = View.VISIBLE
        } else {
            binding.tabReport.btnFindTherapist.visibility = View.GONE
        }

        //check for country
        if (isCountrySupported(requireContext())) {
            binding.tabReport.btnFindTherapist.visibility = View.VISIBLE
        } else {
            binding.tabReport.btnFindTherapist.visibility = View.GONE
        }

        //Recommended actions
        val textRecommendedActions = String.format(
            getString(M3AssessmentManager.getRecommendedActionTextID(intensity)),
            allScore
        )
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvActionDesc.setText(Html.fromHtml(textRecommendedActions, 0))
        } else {
            binding.tabReport.tvActionDesc.setText(Html.fromHtml(textRecommendedActions))
        }

        val selectedOptions = mM3Assessment.getSelectedOptions()
        //suicidal note
        val flag = M3AssessmentManager.hasSuicidalThoughts(selectedOptions.get(4))
        if (flag) {
            /* binding.tabReport.tvSuicideTitle.visibility = View.VISIBLE
             binding.tabReport.tvSuicideContact.visibility = View.VISIBLE
             binding.tabReport.tvSuicideDesc.visibility = View.VISIBLE
             binding.tabReport.viewSeperatorSuicide.visibility = View.VISIBLE*/
            binding.tabReport.layoutSuicide.visibility = View.VISIBLE
        } else {
            /*binding.tabReport.tvSuicideTitle.visibility = View.GONE
            binding.tabReport.tvSuicideContact.visibility = View.GONE
            binding.tabReport.tvSuicideDesc.visibility = View.GONE
            binding.tabReport.viewSeperatorSuicide.visibility = View.GONE*/
            binding.tabReport.layoutSuicide.visibility = View.GONE
        }

        val textContact = getString(R.string.suicide_report_contact)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvSuicideContact.setText(Html.fromHtml(textContact, 0))
        } else {
            binding.tabReport.tvSuicideContact.setText(Html.fromHtml(textContact))
        }

        val text = getString(R.string.suicide_report_details)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvSuicideDesc.setText(Html.fromHtml(text, 0))
        } else {
            binding.tabReport.tvSuicideDesc.setText(Html.fromHtml(text))
        }

        //check for user answers related to question 28 and 29
        val action = M3AssessmentManager.fetchActionsForBlock(
            selectedOptions.get(27),
            selectedOptions.get(28)
        )
        if (action == 0) {
            binding.tabReport.tvBadHabits.visibility = View.GONE
        } else {
            binding.tabReport.tvBadHabits.visibility = View.VISIBLE

            var text = ""
            when (action) {
                //only alcohol
                1 -> {
                    text = getString(R.string.report_details_alcohol)
                }
                //only drug
                2 -> {
                    text = getString(R.string.report_details_drug)
                }
                //both alcohol n drug
                3 -> {
                    text = getString(R.string.report_details_both)
                }
            }

            if (Build.VERSION.SDK_INT >= 24) {
                binding.tabReport.tvBadHabits.setText(Html.fromHtml(text, 0))
            } else {
                binding.tabReport.tvBadHabits.setText(Html.fromHtml(text))
            }
        }

        /*

        val text26 = getString(R.string.quest26_report_details)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvQuest26Desc.setText(Html.fromHtml(text26, 0))
        } else {
            binding.tabReport.tvQuest26Desc.setText(Html.fromHtml(text26))
        }

        val text27 = getString(R.string.quest27_report_details)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvQuest27Desc.setText(Html.fromHtml(text27, 0))
        } else {
            binding.tabReport.tvQuest27Desc.setText(Html.fromHtml(text27))
        }

        val text28 = getString(R.string.quest28_report_details)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvQuest28Desc.setText(Html.fromHtml(text28, 0))
        } else {
            binding.tabReport.tvQuest28Desc.setText(Html.fromHtml(text28))
        }

        val text29 = getString(R.string.quest29_report_details)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvQuest29Desc.setText(Html.fromHtml(text29, 0))
        } else {
            binding.tabReport.tvQuest29Desc.setText(Html.fromHtml(text29))
        }
        */


        val textDisclaimer = getString(R.string.disclaimer_details)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tabReport.tvDisclaimerDesc.setText(Html.fromHtml(textDisclaimer, 0))
        } else {
            binding.tabReport.tvDisclaimerDesc.setText(Html.fromHtml(textDisclaimer))
        }
    }

    private fun initTabLayout() {
        binding.tvTabReport.setOnClickListener {
            binding.tvTabReport.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryColor
                )
            )
            binding.seperatorReport.visibility = View.VISIBLE
            binding.tabReport.root.visibility = View.VISIBLE

            binding.tvTabScore.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.tertaryColor
                )
            )
            binding.seperatorScore.visibility = View.INVISIBLE
            binding.tabScore.root.visibility = View.GONE

        }

        binding.tvTabScore.setOnClickListener {
            binding.tvTabReport.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.tertaryColor
                )
            )
            binding.seperatorReport.visibility = View.INVISIBLE
            binding.tabReport.root.visibility = View.GONE

            binding.tvTabScore.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryColor
                )
            )
            binding.seperatorScore.visibility = View.VISIBLE
            binding.tabScore.root.visibility = View.VISIBLE
        }

    }

    private fun initScoreTab() {

        //fetch questions
        val questions = M3AssessmentManager.instance.getQuestionsForAssessment()

        //set value
        val intensityDep = M3AssessmentManager.getDepressionIntensity(mM3Assessment.depressionScore)
        val intensityAnxiety =
            M3AssessmentManager.getAnxietyIntensity(mM3Assessment.getAnxietyScore())
        val intensityptsd = M3AssessmentManager.getPTSDIntensity(mM3Assessment.ptsdScore)
        val intensityBipolar = M3AssessmentManager.getBipolarIntensity(mM3Assessment.bipolarScore)

        binding.tabScore.diagnosisDep.tvRiskName.text = getString(R.string.depression_risks)
        binding.tabScore.diagnosisDep.tvRiskValue.text = UiUtils.capitalizeString(intensityDep.name)
        binding.tabScore.diagnosisDep.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                M3AssessmentManager.getScoreBgColorID(intensityDep)
            )
        )

        binding.tabScore.diagnosisAnxiety.tvRiskName.text = getString(R.string.anxiety_risks)
        binding.tabScore.diagnosisAnxiety.tvRiskValue.text =
            UiUtils.capitalizeString(intensityAnxiety.name)
        binding.tabScore.diagnosisAnxiety.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                M3AssessmentManager.getScoreBgColorID(intensityAnxiety)
            )
        )

        binding.tabScore.diagnosisPtsd.tvRiskName.text = getString(R.string.ptsd_risks)
        binding.tabScore.diagnosisPtsd.tvRiskValue.text =
            UiUtils.capitalizeString(intensityptsd.name)
        binding.tabScore.diagnosisPtsd.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                M3AssessmentManager.getScoreBgColorID(intensityptsd)
            )
        )

        binding.tabScore.diagnosisBipolar.tvRiskName.text = getString(R.string.bipolar_risks)
        binding.tabScore.diagnosisBipolar.tvRiskValue.text =
            UiUtils.capitalizeString(intensityBipolar.name)
        binding.tabScore.diagnosisBipolar.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                M3AssessmentManager.getScoreBgColorID(intensityBipolar)
            )
        )

        binding.tabScore.question1.tvShortText.text = questions[0].shortText
        binding.tabScore.question2.tvShortText.text = questions[1].shortText
        binding.tabScore.question3.tvShortText.text = questions[2].shortText
        binding.tabScore.question4.tvShortText.text = questions[3].shortText
        binding.tabScore.question5.tvShortText.text = questions[4].shortText
        binding.tabScore.question6.tvShortText.text = questions[5].shortText
        binding.tabScore.question7.tvShortText.text = questions[6].shortText
        binding.tabScore.question8.tvShortText.text = questions[7].shortText
        binding.tabScore.question9.tvShortText.text = questions[8].shortText
        binding.tabScore.question10.tvShortText.text = questions[9].shortText
        binding.tabScore.question11.tvShortText.text = questions[10].shortText
        binding.tabScore.question12.tvShortText.text = questions[11].shortText
        binding.tabScore.question13.tvShortText.text = questions[12].shortText
        binding.tabScore.question14.tvShortText.text = questions[13].shortText
        binding.tabScore.question15.tvShortText.text = questions[14].shortText
        binding.tabScore.question16.tvShortText.text = questions[15].shortText
        binding.tabScore.question17.tvShortText.text = questions[16].shortText
        binding.tabScore.question18.tvShortText.text = questions[17].shortText
        binding.tabScore.question19.tvShortText.text = questions[18].shortText
        binding.tabScore.question20.tvShortText.text = questions[19].shortText
        binding.tabScore.question21.tvShortText.text = questions[20].shortText
        binding.tabScore.question22.tvShortText.text = questions[21].shortText
        binding.tabScore.question23.tvShortText.text = questions[22].shortText
        binding.tabScore.question24.tvShortText.text = questions[23].shortText
        binding.tabScore.question25.tvShortText.text = questions[24].shortText
        binding.tabScore.question26.tvShortText.text = questions[25].shortText
        binding.tabScore.question27.tvShortText.text = questions[26].shortText
        binding.tabScore.question28.tvShortText.text = questions[27].shortText
        binding.tabScore.question29.tvShortText.text = questions[28].shortText

        //check selected options
        val listSelectionNameIDs = mM3Assessment.getSelectionOptionNameIds()

        binding.tabScore.question1.tvAnswer.text = getString(listSelectionNameIDs[0])
        binding.tabScore.question2.tvAnswer.text = getString(listSelectionNameIDs[1])
        binding.tabScore.question3.tvAnswer.text = getString(listSelectionNameIDs[2])
        binding.tabScore.question4.tvAnswer.text = getString(listSelectionNameIDs[3])
        binding.tabScore.question5.tvAnswer.text = getString(listSelectionNameIDs[4])
        binding.tabScore.question6.tvAnswer.text = getString(listSelectionNameIDs[5])
        binding.tabScore.question7.tvAnswer.text = getString(listSelectionNameIDs[6])
        binding.tabScore.question8.tvAnswer.text = getString(listSelectionNameIDs[7])
        binding.tabScore.question9.tvAnswer.text = getString(listSelectionNameIDs[8])
        binding.tabScore.question10.tvAnswer.text = getString(listSelectionNameIDs[9])
        binding.tabScore.question11.tvAnswer.text = getString(listSelectionNameIDs[10])
        binding.tabScore.question12.tvAnswer.text = getString(listSelectionNameIDs[11])
        binding.tabScore.question13.tvAnswer.text = getString(listSelectionNameIDs[12])
        binding.tabScore.question14.tvAnswer.text = getString(listSelectionNameIDs[13])
        binding.tabScore.question15.tvAnswer.text = getString(listSelectionNameIDs[14])
        binding.tabScore.question16.tvAnswer.text = getString(listSelectionNameIDs[15])
        binding.tabScore.question17.tvAnswer.text = getString(listSelectionNameIDs[16])
        binding.tabScore.question18.tvAnswer.text = getString(listSelectionNameIDs[17])
        binding.tabScore.question19.tvAnswer.text = getString(listSelectionNameIDs[18])
        binding.tabScore.question20.tvAnswer.text = getString(listSelectionNameIDs[19])
        binding.tabScore.question21.tvAnswer.text = getString(listSelectionNameIDs[20])
        binding.tabScore.question22.tvAnswer.text = getString(listSelectionNameIDs[21])
        binding.tabScore.question23.tvAnswer.text = getString(listSelectionNameIDs[22])
        binding.tabScore.question24.tvAnswer.text = getString(listSelectionNameIDs[23])
        binding.tabScore.question25.tvAnswer.text = getString(listSelectionNameIDs[24])
        binding.tabScore.question26.tvAnswer.text = getString(listSelectionNameIDs[25])
        binding.tabScore.question27.tvAnswer.text = getString(listSelectionNameIDs[26])
        binding.tabScore.question28.tvAnswer.text = getString(listSelectionNameIDs[27])
        binding.tabScore.question29.tvAnswer.text = getString(listSelectionNameIDs[28])


        val listSelectionColorIDs = mM3Assessment.getSelectionOptionColorIds()
        binding.tabScore.question1.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[0]
            )
        )
        binding.tabScore.question2.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[1]
            )
        )
        binding.tabScore.question3.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[2]
            )
        )
        binding.tabScore.question4.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[3]
            )
        )
        binding.tabScore.question5.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[4]
            )
        )
        binding.tabScore.question6.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[5]
            )
        )
        binding.tabScore.question7.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[6]
            )
        )
        binding.tabScore.question8.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[7]
            )
        )
        binding.tabScore.question9.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[8]
            )
        )
        binding.tabScore.question10.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[9]
            )
        )
        binding.tabScore.question11.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[10]
            )
        )
        binding.tabScore.question12.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[11]
            )
        )
        binding.tabScore.question13.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[12]
            )
        )
        binding.tabScore.question14.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[13]
            )
        )
        binding.tabScore.question15.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[14]
            )
        )
        binding.tabScore.question16.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[15]
            )
        )
        binding.tabScore.question17.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[16]
            )
        )
        binding.tabScore.question18.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[17]
            )
        )
        binding.tabScore.question19.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[18]
            )
        )
        binding.tabScore.question20.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[19]
            )
        )
        binding.tabScore.question21.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[20]
            )
        )
        binding.tabScore.question22.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[21]
            )
        )
        binding.tabScore.question23.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[22]
            )
        )
        binding.tabScore.question24.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[23]
            )
        )
        binding.tabScore.question25.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[24]
            )
        )
        binding.tabScore.question26.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[25]
            )
        )
        binding.tabScore.question27.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[26]
            )
        )
        binding.tabScore.question28.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[27]
            )
        )
        binding.tabScore.question29.circleRisk.setColor(
            ContextCompat.getColor(
                requireContext(),
                listSelectionColorIDs[28]
            )
        )

    }


}