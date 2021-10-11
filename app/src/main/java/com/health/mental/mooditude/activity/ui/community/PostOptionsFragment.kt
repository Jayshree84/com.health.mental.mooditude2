package com.health.mental.mooditude.activity.ui.community

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.BottomSheetMenuPostBinding

class PostOptionsFragment(val mListener: ItemClickListener,
                          val isOwnPost: Boolean): BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetMenuPostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetMenuPostBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
    }

    private fun setUpViews(view: View) {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }

        binding.menuEdit.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        binding.menuDelete.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        binding.menuReport.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        if(!isOwnPost) {
            binding.menuEdit.isEnabled = false
            binding.menuEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor))

            binding.menuDelete.isEnabled = false
            binding.menuDelete.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
        }
        else {
            binding.menuReport.visibility = View.GONE
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*if (context is ItemClickListener) {
            mListener = context as ItemClickListener
        } else {
            throw RuntimeException(
                context.toString()
                    .toString() + " must implement ItemClickListener"
            )
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        //mListener = null
    }
    interface ItemClickListener {
        fun onItemClick(itemId: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: ItemClickListener, isOwnPost:Boolean): PostOptionsFragment {
            val fragment = PostOptionsFragment(listener, isOwnPost)
            return fragment
        }
    }
}