package com.health.mental.mooditude.activity

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable.ShaderFactory
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.data.model.M3Question
import com.health.mental.mooditude.databinding.ActivityMassessmentFlowDialogBinding
import com.health.mental.mooditude.fragment.MAssessmentQuestFragment
import com.health.mental.mooditude.utils.RESULT_ASSESSMENT_FINISHED
import java.util.*


class MAssessmentFlowDialog : BaseActivity() {

    private lateinit var mListQuestions: ArrayList<M3Question>
    private var mCurrQuestionPos = 0
    private val mTotalQuestions = 25
    private var mCurrQuestion: M3Question? = null

    private lateinit var binding: ActivityMassessmentFlowDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMassessmentFlowDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {
        /*val lp = window.attributes
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 1).toInt()
        val screenHeight = (metrics.heightPixels * 1).toInt()
        lp.width = screenWidth
        lp.height = screenHeight //WindowManager.LayoutParams.MATCH_PARENT*/

        //Let's first fetch assessment questions
        mListQuestions = M3AssessmentManager.instance.getQuestionsForAssessment()

        mCurrQuestionPos = 0
        mCurrQuestion = mListQuestions.get(mCurrQuestionPos)

        binding.toolbar.progress.scaleY = 3f
        //enable/disable - back n next
        binding.toolbar.btnClose.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.toolbar.btnForward.setOnClickListener {
            super.onBackPressed()
            //Let's move to previous question
            mCurrQuestionPos--
            mCurrQuestion = mListQuestions.get(mCurrQuestionPos)

            updateUi()
            //setBackgroundGradient()
        }
        binding.toolbar.btnNext2.setOnClickListener {
            submitAnswer()
        }

        updateUi()
        setBackgroundGradient()
        //Add first fragment
        val fragment = MAssessmentQuestFragment.newInstance(mCurrQuestion!!)
        addFragment(R.id.layout_container, fragment!!, true)
    }

    private fun updateUi() {
        //Set total questions
        binding.tvTotalQuest.text = String.format("%d of %d", mCurrQuestionPos + 1, mTotalQuestions)
        //set progress
        binding.toolbar.progress.max = mTotalQuestions + 1
        binding.toolbar.progress.progress = mCurrQuestionPos + 1

        if (mCurrQuestion!!.selectedOption != null) {
            setNextEnabled(true)
        } else {
            setNextEnabled(false)
        }
        if (mCurrQuestionPos > 0) {
            setForwardEnabled(true)
        } else {
            setForwardEnabled(false)
        }

        //setBackgroundGradient()
    }

    public fun setBackgroundGradient() {

        val layout = binding.mainLayout
        if (mCurrQuestion!!.selectedOption == null) {
            layout.background =
                ColorDrawable(ResourcesCompat.getColor(resources, R.color.gradient_bg_start, null))
            return
        }

        var selection = 0F

        when (mCurrQuestion!!.selectedOption!!) {
            0 -> {
                selection = 0.8F
            }
            1 -> {
                selection = 0.6F
            }
            2 -> {
                selection = 0.4F
            }
            3 -> {
                selection = 0.2F
            }
            4 -> {
                selection = 0F
            }
        }

        /* val gd = GradientDrawable()
         gd.colors = intArrayOf(ResourcesCompat.getColor(resources, R.color.gradient_bg_start, null),
             ResourcesCompat.getColor(resources, R.color.gradient_bg_end, null),
             ResourcesCompat.getColor(resources, R.color.gradient_bg_end, null))

         gd.cornerRadius = 0f
         gd.gradientType = GradientDrawable.LINEAR_GRADIENT
         gd.setGradientCenter(0f, 0.5f)*/


        /*val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(ResourcesCompat.getColor(resources, R.color.white, null),
                ResourcesCompat.getColor(resources, R.color.black, null))
        )*/

        //layout.background = gd

        /* val colors = intArrayOf(ResourcesCompat.getColor(resources, R.color.gradient_bg_start, null),
             ResourcesCompat.getColor(resources, R.color.gradient_bg_end, null),
             ResourcesCompat.getColor(resources, R.color.gradient_bg_end, null))
         val gd: Drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
         val sd: Drawable = ScaleDrawable(gd, Gravity.TOP, 0f, 0f)
         sd.level = 0
         layout.background = gd
 */

        val sf: ShaderFactory = object : ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    (width / 2).toFloat(),
                    0f,
                    (width / 2).toFloat(),
                    height.toFloat(),
                    intArrayOf(
                        ResourcesCompat.getColor(resources, R.color.gradient_bg_start, null),
                        ResourcesCompat.getColor(resources, R.color.gradient_bg_end, null),
                        ResourcesCompat.getColor(resources, R.color.gradient_bg_end, null)
                    ),
                    floatArrayOf(0f, selection, 1f),  // start, center and end position
                    Shader.TileMode.CLAMP
                )
            }
        }

        val pd = PaintDrawable()
        pd.shape = RectShape()
        pd.shaderFactory = sf

        layout.background = pd
    }


    fun submitAnswer() {
        //setForwardEnabled(true)
        //setNextEnabled(false)

        //Let's move to next question
        mCurrQuestionPos++

        //check for max questions
        if (mCurrQuestionPos >= mTotalQuestions) {
            //show different page
            setResult(RESULT_ASSESSMENT_FINISHED)
            showScoreCalculatingPage(intent.extras!!.getBoolean("show_home"))
            return
        }

        mCurrQuestion = mListQuestions.get(mCurrQuestionPos)

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(MAssessmentQuestFragment::class.java.simpleName),
            MAssessmentQuestFragment.newInstance(mCurrQuestion!!)!!,
            true
        )
        updateUi()
        //setBackgroundGradient()
    }

    /**
     * Enables/disables NEXT button
     */
    fun setNextEnabled(flag: Boolean) {
        binding.toolbar.btnNext2.isEnabled = flag
    }

    /**
     * Enables/disables FORWARD button
     */
    fun setForwardEnabled(flag: Boolean) {
        binding.toolbar.btnForward.isEnabled = flag
    }

    fun answerSelected(selectedOption: Int?) {
        this.mCurrQuestion!!.selectedOption = selectedOption
        if (selectedOption != null && selectedOption != -1) {
            setNextEnabled(true)
        } else {
            setNextEnabled(false)
        }
        setBackgroundGradient()

        //call next directly
        binding.toolbar.btnNext2.callOnClick()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if(binding.toolbar.btnForward.isEnabled) {
            binding.toolbar.btnForward.callOnClick()
        }
        else {
            super.onBackPressed()
        }
    }
}