package com.health.mental.mooditude.activity.ui.community

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.health.mental.mooditude.databinding.BottomSheetMenuCommunityBinding

class OptionsFragment(val mListener: ItemClickListener): BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetMenuCommunityBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetMenuCommunityBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
    }

    private fun setUpViews(view: View) {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }

        binding.menuFresh.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        binding.menuPopular.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        binding.menuActive.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        binding.menuYourPosts.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        binding.menuYourBookmark.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }
        binding.menuReported.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
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
        fun newInstance(listener: ItemClickListener): OptionsFragment {
            val fragment = OptionsFragment(listener)
            return fragment
        }
    }
}