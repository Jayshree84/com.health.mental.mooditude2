package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.FindMyTherapistActivity
import com.health.mental.mooditude.databinding.FragmentHealthTherapistsBinding
import com.health.mental.mooditude.fragment.BaseFragment

/**
 * A simple [Fragment] subclass.
 * Use the [HealthTherapistsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HealthTherapistsFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentHealthTherapistsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHealthTherapistsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnNeedTherapist.setOnClickListener {
            (requireActivity() as FindMyTherapistActivity).onFindTherapistBtnClicked()
        }

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvInfo.setText(Html.fromHtml(getString(R.string.enjoy_working), 0))
        } else {
            binding.tvInfo.setText(Html.fromHtml(getString(R.string.enjoy_working)))
        }
        return root
    }

}