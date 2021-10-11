package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.data.model.TherapistRequestInfo
import com.health.mental.mooditude.databinding.FragmentRequestCommentBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [RequestCommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestCommentFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentRequestCommentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRequestCommentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    fun updateCommentMessage(requestInfo: TherapistRequestInfo): Boolean {
        requestInfo.comment = binding.etMessage.text.toString()
        return true
    }


}