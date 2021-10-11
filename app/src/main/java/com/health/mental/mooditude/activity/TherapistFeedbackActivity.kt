package com.health.mental.mooditude.activity

import android.os.Bundle
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.TherapistFeedback
import com.health.mental.mooditude.fragment.requesttherapist.TherapistRequestPositiveFeedbackFragment
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.ratedTherapyRequest
import org.jetbrains.anko.indeterminateProgressDialog

class TherapistFeedbackActivity : BaseActivity() {

    private var mRequestId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_therapist_feedback)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        setPageTitle(findViewById(R.id.toolbar), getString(R.string.feedback))

        val bundle = intent.extras!!
        mRequestId = bundle.getString("id").toString()
        val fragment = TherapistRequestPositiveFeedbackFragment()
        addFragment(R.id.layout_container, fragment, true)
    }

    fun onSubmitFeedbackBtnClicked(feedback: TherapistFeedback) {
        val dlg = indeterminateProgressDialog(getString(R.string.sending_feedback))
        DBManager.instance.uploadTherapistFeedback(
            mRequestId,
            feedback,
            object : FBQueryCompletedListener {
                override fun onResultReceived(result: Any?) {
                    dlg.dismiss()

                    if (result != null) {
                        EventCatalog.instance.ratedTherapyRequest(feedback.rating, true, mRequestId)
                        finish()
                    }
                }
            })
    }
}