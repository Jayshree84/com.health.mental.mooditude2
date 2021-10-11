package com.health.mental.mooditude.activity.ui.tracking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.health.mental.mooditude.R

class OptionsBottomSheetFragment(val mListener: ItemClickListener): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_menu_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mode = 0 //Week tab
        if(arguments != null) {
            mode = requireArguments().getInt("mode")
        }
        setUpViews(view, mode)
    }

    private fun setUpViews(view: View, mode:Int) {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
        view.findViewById<View>(R.id.menu_science).setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        view.findViewById<View>(R.id.menu_full_report).setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        view.findViewById<View>(R.id.menu_add_new).setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        view.findViewById<View>(R.id.menu_see_past_assessment).setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(it.id)
        }

        //Month tab
        if(mode == 1) {
            view.findViewById<View>(R.id.menu_full_report).visibility = View.GONE
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
        fun newInstance(bundle: Bundle, listener: ItemClickListener): OptionsBottomSheetFragment {
            val fragment = OptionsBottomSheetFragment(listener)
            fragment.arguments = bundle
            return fragment
        }
    }
}