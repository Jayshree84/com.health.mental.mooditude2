package com.health.mental.mooditude.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import com.health.mental.mooditude.R

class CustomDialogActivity : AppCompatActivity() {

    private var mDialog:CustomDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_custom_dialog)

        val dlg = CustomDialog(this)
        dlg.setCanceledOnTouchOutside(false)
        dlg.show()
        dlg.setOnDismissListener {
            this.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mDialog?.dismiss()
    }

    inner class CustomDialog(context: Context) : AppCompatDialog(context) {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.custom_dialog)
            window!!.setDimAmount(0.0f)
            val lp = window!!.attributes
            val metrics = resources.displayMetrics
            val screenWidth = (metrics.widthPixels * 0.95).toInt()
            lp.width = screenWidth
            //lp.height = screenHeight

            findViewById<View>(R.id.btn_ok)!!.setOnClickListener {
                onOkPressed()
            }

            val tvTitle = findViewById<TextView>(R.id.tv_title)
            if (Build.VERSION.SDK_INT >= 24) {
                tvTitle!!.setText(Html.fromHtml(getString(R.string.science_behind_assessment), 0))
            } else {
                tvTitle!!.setText(Html.fromHtml(getString(R.string.science_behind_assessment)))
            }

            val tvDesc = findViewById<TextView>(R.id.tv_desc)
            if (Build.VERSION.SDK_INT >= 24) {
                tvDesc!!.setText(Html.fromHtml(getString(R.string.m3_checklist), 0))
            } else {
                tvDesc!!.setText(Html.fromHtml(getString(R.string.m3_checklist)))
            }
            tvDesc.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private fun onOkPressed() {
            this@CustomDialogActivity.onBackPressed()
        }

        override fun onBackPressed() {
            //super.onBackPressed()
            onOkPressed()
        }

    }
}