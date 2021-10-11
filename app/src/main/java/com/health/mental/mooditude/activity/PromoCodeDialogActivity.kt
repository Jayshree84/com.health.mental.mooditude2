package com.health.mental.mooditude.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableResult
import com.health.mental.mooditude.R
import com.health.mental.mooditude.cloudfunction.FBFunctionsHelper
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.validatePromoCode
import org.jetbrains.anko.indeterminateProgressDialog

class PromoCodeDialogActivity : BaseActivity() {

    private var mDialog: CustomDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promocode_dialog)

        mDialog = CustomDialog(this)
        mDialog!!.setCanceledOnTouchOutside(false)
        mDialog!!.show()
        mDialog!!.setOnDismissListener {
            this.finish()
        }
    }

    override fun initComponents() {

    }

    override fun onDestroy() {
        super.onDestroy()

        mDialog?.dismiss()
    }

    inner class CustomDialog(context: Context) : AppCompatDialog(context), TextWatcher {

        private var mTvErrorInfo: TextView? = null
        private var mEditCode: EditText? = null
        private var mBtnConfirm: AppCompatButton? = null
        private var mBtnDontHaveCode: AppCompatButton? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.fragment_email)
            window!!.setDimAmount(0.5f)
            val lp = window!!.attributes
            val metrics = resources.displayMetrics
            val screenWidth = (metrics.widthPixels * 0.95).toInt()
            lp.width = screenWidth
            //lp.height = screenHeight

            mTvErrorInfo = findViewById(R.id.tv_error_info)
            mEditCode = findViewById(R.id.et_promo_code)
            mBtnConfirm = findViewById(R.id.btn_confirm)
            mBtnDontHaveCode = findViewById(R.id.btn_dont_have_code)

            //First reset error text
            mTvErrorInfo!!.text = ""

            mBtnDontHaveCode!!.setOnClickListener {
                (this@PromoCodeDialogActivity as BaseActivity).showWelcomeUserPage()
            }

            mBtnConfirm!!.setOnClickListener {


                //Let's first validate all texts
                val promoCode = mEditCode!!.text.toString()
                mTvErrorInfo!!.text = ""
                UiUtils.hideKeyboard(this@PromoCodeDialogActivity)

                if (validatePromoCode(promoCode)) {

                    //Code validated,
                    val progressDlg =
                        context.indeterminateProgressDialog(context.getString(R.string.validating_code))
                    FBFunctionsHelper.instance.processInvitationCode(promoCode,
                        listener = object : FBQueryCompletedListener {
                            override fun onResultReceived(result: Any?) {
                                progressDlg.dismiss()
                                val task = result as Task<HttpsCallableResult>
                                if (!task.isSuccessful) {
                                    val e = task.exception
                                    mTvErrorInfo!!.text = e!!.localizedMessage
                                    if (e is FirebaseFunctionsException) {
                                        val code = e.code
                                        val details = e.details

                                        errorLog(TAG, "Error in code validation : " + code + " : " + details)
                                        mTvErrorInfo!!.text = e.localizedMessage
                                    }
                                } else {
                                    //task successfull
                                    //now check for body
                                    if (task.result != null) {
                                        val resultData = task.result

                                        val map = task.result!!.data as HashMap<String, Any>

                                        //check if error is present
                                        if (map.containsKey("error")) {
                                            val error = map.get("error") as HashMap<String, Any>
                                            val msg = error.get("message")
                                            if(msg != null && msg is String) {
                                                errorLog(TAG, "Error in code validation : " + error.toString() + " : " + msg)
                                                mTvErrorInfo!!.text = msg
                                            }
                                        } else {
                                            //success
                                            (this@PromoCodeDialogActivity as BaseActivity).invitationCodeProccessed(
                                                promoCode
                                            )
                                        }

                                    }
                                }
                            }
                        }


                    )
                } else {
                    mTvErrorInfo!!.text = context.getString(R.string.invalid_code)
                    mEditCode!!.requestFocus()
                }
            }


            setConfirmEnabled(false)
            mEditCode!!.addTextChangedListener(this)

            mEditCode!!.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
                if (motionEvent != null && motionEvent.action == MotionEvent.ACTION_DOWN) {
                    if (motionEvent.x >= (view.width - (view as EditText).compoundPaddingRight)) {
                        view.setText("")

                        //show keyboard
                        view.requestFocus()
                    }
                }
                return@OnTouchListener false
            })
            //editor event listener
            mEditCode!!.setOnEditorActionListener { v, actionId, event ->
                if (event == null || event.action == MotionEvent.ACTION_UP) {
                    if (mBtnConfirm!!.isEnabled) {
                        mBtnConfirm!!.callOnClick()
                    }
                }
                return@setOnEditorActionListener true
            }

            UiUtils.showKeyboard(context)
        }

        /**
         * Enables/disables NEXT button
         */
        fun setConfirmEnabled(flag: Boolean) {
            mBtnConfirm!!.isEnabled = flag
        }

        override fun onBackPressed() {
            super.onBackPressed()
            mBtnDontHaveCode!!.callOnClick()
        }

        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //Reset error
            mTvErrorInfo!!.text = ""

            if (mEditCode!!.text!!.trim().length < 4) {
                setConfirmEnabled(false)
            } else {
                setConfirmEnabled(true)
            }

            //check length and show/hide clear button
            updateClearButton(mEditCode!!, mEditCode!!.text!!)
        }

        private fun updateClearButton(editText: EditText, s: CharSequence) {
            if (s.length > 0) {
                editText.setCompoundDrawablesWithIntrinsicBounds(
                    null, null,
                    ContextCompat.getDrawable(context, R.drawable.ic_clear), null
                )
            } else {
                editText.setCompoundDrawables(null, null, null, null)
            }
        }

    }
}