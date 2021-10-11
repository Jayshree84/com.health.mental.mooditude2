package com.health.mental.mooditude.activity.ui.care

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.FragmentCareStateBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [StateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StateFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentCareStateBinding
    private var mSelectedState = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCareStateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvStatePreference.setOnClickListener {
            showStateListDialog(mSelectedState)
        }
        binding.tvState.setOnClickListener {
            binding.tvStatePreference.callOnClick()
        }

        binding.btnContinue.setOnClickListener {
            if(mSelectedState.trim().isEmpty()) {
                Toast.makeText(requireContext(), "Please select state", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            (parentFragment as CareFragment).onStateSelected(mSelectedState)
        }

        setContinueEnabled()
        return root
    }

    private fun showStateListDialog(state:String) {
        //Initialize the Alert Dialog
        val context = requireActivity()
        val builder = AlertDialog.Builder(context)
        //Source of the data in the DIalog
        val array = context.resources.getStringArray(R.array.usa_states)

        val dialogTitle = (context as Activity).layoutInflater.inflate(R.layout.select_dialog_title, null) as TextView
        dialogTitle.text = getString(R.string.select_state).uppercase()

        val adapter = ArrayAdapter<String>(context,
            R.layout.select_dialog_singlechoice, android.R.id.text1, array)

        // Set the dialog title
        builder.setCustomTitle(dialogTitle)
            // Specify the list array, the items to be selected by default (null for none),
            // and the listener through which to receive callbacks when items are selected
            .setSingleChoiceItems(adapter, array.indexOf(state), object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {

                    dialog.dismiss()

                    //call API again
                    mSelectedState = array[which]
                    binding.tvStatePreference.text = mSelectedState
                    setContinueEnabled()
                }
            })

        builder.create().show()
    }

    fun setContinueEnabled() {
        binding.btnContinue.isEnabled = mSelectedState.trim().isNotEmpty()
    }



}