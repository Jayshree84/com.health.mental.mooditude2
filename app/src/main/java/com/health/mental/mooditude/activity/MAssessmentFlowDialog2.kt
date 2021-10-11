package com.health.mental.mooditude.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.data.model.M3Question
import com.health.mental.mooditude.debugLog

class MAssessmentFlowDialog2 : BaseActivity() {

    private var mDialog: CustomDialog? = null
    private var mQuestion: M3Question? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_massessment_flow_dialog2)

        val pos = intent.extras!!.getInt("position")
        mQuestion = M3AssessmentManager.instance.getQuestion(pos!!)
        if(mQuestion == null) {
            this.finish()
            return
        }

        debugLog(TAG, "Question is : " + mQuestion!!.toString())
        debugLog(TAG, "First Question time to answer time is : " + mQuestion!!.timeToAnswerInSeconds)
        //initActionBar(findViewById(R.id.toolbar))
        initComponents()
    }

    override fun initComponents() {

        mDialog = CustomDialog(this)
        mDialog!!.setCanceledOnTouchOutside(false)
        mDialog!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()

        mDialog?.dismiss()
    }

    inner class CustomDialog(context: Context) : AppCompatDialog(context) {

        private val mQuestionAppearTime = System.currentTimeMillis()
        private var mSelectedTextView: TextView? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.dialog_assessment_quest)

            val lp = window!!.attributes
            val metrics = resources.displayMetrics
            val screenWidth = (metrics.widthPixels * 0.95).toInt()
            val screenHeight = (metrics.heightPixels * 0.95).toInt()
            lp.width = screenWidth
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT

            val tvMostTime = findViewById<TextView>(R.id.tv_most_time)
            if (Build.VERSION.SDK_INT >= 24) {
                tvMostTime!!.setText(Html.fromHtml(getString(R.string.most_of_the_time2), 0))
            } else {
                tvMostTime!!.setText(Html.fromHtml(getString(R.string.most_of_the_time2)))
            }

            val tvTitle = findViewById<TextView>(R.id.tv_title)
            tvTitle!!.text = mQuestion!!.text
            findViewById<View>(R.id.btn_next)!!.isEnabled = false

            findViewById<View>(R.id.btn_next)!!.setOnClickListener {
                this.dismiss()
                setResult(RESULT_OK)
                finish()
            }

            findViewById<TextView>(R.id.tv_option1)!!.setOnClickListener {
                onAnswerSelected(0, it as TextView)
            }
            findViewById<TextView>(R.id.tv_option2)!!.setOnClickListener {
                onAnswerSelected(1, it as TextView)
            }
            findViewById<TextView>(R.id.tv_option3)!!.setOnClickListener {
                onAnswerSelected(2, it as TextView)
            }
            findViewById<TextView>(R.id.tv_option4)!!.setOnClickListener {
                onAnswerSelected(3, it as TextView)
            }
            findViewById<TextView>(R.id.tv_option5)!!.setOnClickListener {
                onAnswerSelected(4, it as TextView)
            }
        }

        override fun onBackPressed() {
            //super.onBackPressed()

        }

        private fun selectOption(text: TextView) {
            text.setTextColor(ContextCompat.getColor(context, R.color.white))
            text.background = ContextCompat.getDrawable(context, R.drawable.option_val_bg_selected)
            mSelectedTextView = text
        }

        private fun deSelectOption(text: TextView) {
            text.setTextColor(ContextCompat.getColor(context, R.color.secondaryColor))
            text.background = ContextCompat.getDrawable(context, R.drawable.option_val_bg)
        }

        private fun onAnswerSelected(position: Int, text: TextView) {
            findViewById<View>(R.id.btn_next)!!.isEnabled = true
            if (position >= 0) {
                //setNextEnabled(true)
                mQuestion!!.selectedOption = position
                debugLog(TAG,
                    "Selected answer is : " + mQuestion!!.options.get(position).id + ": " + mQuestion!!.options.get(
                        position
                    ).text
                )
                //save time to answer
                val milliSeconds = System.currentTimeMillis() - mQuestionAppearTime
                mQuestion!!.timeToAnswerInSeconds = (milliSeconds/1000).toInt()
                debugLog(TAG, "Question time to answer time : " + mQuestion!!.timeToAnswerInSeconds)
            } else {
                //setNextEnabled(false)
                mQuestion!!.selectedOption = -1
            }
            //(requireActivity() as MAssessmentFlowDialog).answerSelected(mQuestion!!.selectedOption)

            if (mSelectedTextView != null) {
                deSelectOption(mSelectedTextView!!)
            }
            selectOption(text)
        }

    }
}