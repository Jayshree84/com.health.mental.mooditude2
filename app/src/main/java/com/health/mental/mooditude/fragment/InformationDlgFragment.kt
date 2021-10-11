package com.health.mental.mooditude.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.learnMoreAboutM3
import com.health.mental.mooditude.services.instrumentation.viewedPastAssessments


class InformationDlgFragment(): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.bottom_sheet_information, container, false)

        root.findViewById<View>(R.id.btn_close)!!.setOnClickListener {
            onOkPressed()
        }

        val tvTitle = root.findViewById<TextView>(R.id.tv_title)
        if (Build.VERSION.SDK_INT >= 24) {
            tvTitle!!.setText(Html.fromHtml(getString(R.string.science_behind_assessment), 0))
        } else {
            tvTitle!!.setText(Html.fromHtml(getString(R.string.science_behind_assessment)))
        }

        val tvDesc = root.findViewById<TextView>(R.id.tv_desc)
        if (Build.VERSION.SDK_INT >= 24) {
            tvDesc!!.setText(Html.fromHtml(getString(R.string.science_desc), 0))
        } else {
            tvDesc!!.setText(Html.fromHtml(getString(R.string.science_desc)))
        }

        val tvDesc2 = root.findViewById<TextView>(R.id.tv_desc2)
        if (Build.VERSION.SDK_INT >= 24) {
            tvDesc2!!.setText(Html.fromHtml(getString(R.string.m3_checklist), 0))
        } else {
            tvDesc2!!.setText(Html.fromHtml(getString(R.string.m3_checklist)))
        }
        tvDesc2.setMovementMethod(LinkMovementMethod.getInstance());

        //log event
        EventCatalog.instance.learnMoreAboutM3()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state =
                BottomSheetBehavior.STATE_EXPANDED
        }

        // Do something with your dialog like setContentView() or whatever
        return dialog
    }

    private fun onOkPressed() {
        dismiss()
    }



}