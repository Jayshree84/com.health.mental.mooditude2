package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.adapter.TherapistPreferenceAdapter
import com.health.mental.mooditude.custom.ItemMoveCallback
import com.health.mental.mooditude.databinding.FragmentTherapistPreferenceBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [TherapistPreferenceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TherapistPreferenceFragment : BaseFragment(), TherapistPreferenceAdapter.StartDragListener {

    private var mListEntries = ArrayList<TherapistPreference>()
    private lateinit var mTouchHelper: ItemTouchHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentTherapistPreferenceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTherapistPreferenceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createPreferenceList()

        val adapter = TherapistPreferenceAdapter(requireContext(), mListEntries, this)

        val callback: ItemTouchHelper.Callback = ItemMoveCallback(adapter)
        mTouchHelper = ItemTouchHelper(callback)
        mTouchHelper.attachToRecyclerView(binding.listEntries)

        binding.listEntries.setAdapter(adapter)

        //listview
        val recyclerLayoutManager = LinearLayoutManager(requireActivity())
        binding.listEntries.setLayoutManager(recyclerLayoutManager)

        return root
    }


    private fun createPreferenceList() {
        mListEntries = ArrayList<TherapistPreference>()

        mListEntries.add(
            TherapistPreference(getString(R.string.therapist_preference1_title),
            getString(R.string.therapist_preference1_desc),
                getString(R.string.therapist_preference1_shortext),
                1))

        mListEntries.add(
        TherapistPreference(getString(R.string.therapist_preference2_title),
            getString(R.string.therapist_preference2_desc),
            getString(R.string.therapist_preference2_shortext),2))

        mListEntries.add(
        TherapistPreference(getString(R.string.therapist_preference3_title),
            getString(R.string.therapist_preference3_desc),
            getString(R.string.therapist_preference3_shortext),  3))

        mListEntries.add(
        TherapistPreference(getString(R.string.therapist_preference4_title),
            getString(R.string.therapist_preference4_desc),
            getString(R.string.therapist_preference4_shortext),4))

        mListEntries.add(
        TherapistPreference(getString(R.string.therapist_preference5_title),
            getString(R.string.therapist_preference5_desc),
            getString(R.string.therapist_preference5_shortext),5))

    }

    inner class TherapistPreference(
        val title: String = "",
        val desc: String = "",
        val shortText:String="",
        val order: Int = 0
    ){}

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        mTouchHelper.startDrag(viewHolder!!)
    }

    //find entries
    fun getPreferences():ArrayList<TherapistPreference> {
        return mListEntries
    }
}