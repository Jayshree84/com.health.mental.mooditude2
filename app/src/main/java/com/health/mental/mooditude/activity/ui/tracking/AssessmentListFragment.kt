package com.health.mental.mooditude.activity.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.health.mental.mooditude.adapter.AssessmentListAdapter
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.databinding.FragmentTrackingListBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.OptionSelectListener

class AssessmentListFragment : BaseFragment() {

    companion object {
        fun newInstance() = AssessmentListFragment()
    }

    private val binding get() = _binding!! as FragmentTrackingListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        DBManager.instance.getAssessmentList().observe(viewLifecycleOwner, Observer {
            val list = it

            val adapter = AssessmentListAdapter(list, requireContext(), object : OptionSelectListener {
                override fun onOptionSelected(position: Int) {
                    //show detail page
                    (parentFragment as TrackingFragment).onViewFullReportFromListClicked(list[position])
                }

            })
            binding.listEntries.adapter = adapter
        })

        //listview
        val recyclerLayoutManager = LinearLayoutManager(requireActivity())
        binding.listEntries.setLayoutManager(recyclerLayoutManager)

        return root
    }

}