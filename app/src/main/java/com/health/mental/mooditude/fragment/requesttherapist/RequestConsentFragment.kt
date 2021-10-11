package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.FindMyTherapistActivity
import com.health.mental.mooditude.databinding.FragmentRequestConsentBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [RequestConsentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestConsentFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentRequestConsentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRequestConsentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val text = getString(R.string.consent_desc)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvDesc.setText(Html.fromHtml(text, 0))
        } else {
            binding.tvDesc.setText(Html.fromHtml(text))
        }

        binding.btnSubmit.setOnClickListener {
            (requireActivity() as FindMyTherapistActivity).onSubmitRequestBtnClicked()
        }
        return root
    }




}