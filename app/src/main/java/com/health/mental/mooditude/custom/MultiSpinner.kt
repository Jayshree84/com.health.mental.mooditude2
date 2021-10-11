package com.health.mental.mooditude.custom

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.model.UserChallenge
import com.health.mental.mooditude.debugLog


/**
 * Created by Jayshree Rathod on 29,September,2021
 */
class MultiSpinner : AppCompatSpinner, OnMultiChoiceClickListener {
    private var items: List<String>? = null
    private var itemValues: List<Any>? = null
    private lateinit var selected: BooleanArray
    private lateinit var selectedSaved: BooleanArray
    private var defaultText: String? = null
    private var listener: MultiSpinnerListener? = null

    constructor(context: Context) : super(context) {}
    constructor(arg0: Context, arg1: AttributeSet?) : super(arg0, arg1) {}
    constructor(arg0: Context, arg1: AttributeSet?, arg2: Int) : super(arg0, arg1, arg2) {}

    override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
        if (isChecked) selected[which] = true else selected[which] = false
    }

    private fun saveSelection() {
        // refresh text on spinner
        val spinnerBuffer = StringBuffer()
        var someSelected = false
        for (i in items!!.indices) {
            if (selected[i] == true) {
                spinnerBuffer.append(items!![i])
                spinnerBuffer.append(", ")
                someSelected = true
            }
        }
        var spinnerText: String?
        if (someSelected) {
            spinnerText = spinnerBuffer.toString()
            if (spinnerText.length > 2) spinnerText =
                spinnerText.substring(0, spinnerText.length - 2)
        } else {
            spinnerText = defaultText
        }
        val adapter = ArrayAdapter(
            context,
            R.layout.select_dialog_singlechoice, arrayOf(spinnerText)
        )
        setAdapter(adapter)
        listener!!.onItemsSelected(selected)

        //change saveselected
       selectedSaved = selected.clone()
    }

    override fun performClick(): Boolean {
        selected = selectedSaved.clone()
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.DialogTheme)
        builder.setMultiChoiceItems(
            items!!.toTypedArray(), selected, this
        )

        builder.setPositiveButton(
            R.string.ok_btn,
            DialogInterface.OnClickListener { dialog, which -> saveSelection() })
        builder.show()
        debugLog("spinner", "Perfomrclick : ")
        for(i in selected.indices) {
            debugLog("TAG", "value : " + selected[i])
        }
        return true
    }

    fun setItems(
        items: List<String>, itemVals:List<Any>, allText: String?, defaultText:String?,
        listener: MultiSpinnerListener?
    ) {
        this.items = items
        this.itemValues = itemVals
        this.defaultText = defaultText
        this.listener = listener

        // all selected by default
        selected = BooleanArray(items.size)
        selectedSaved = BooleanArray(items.size)
        //for (i in selected.indices) selected[i] = true

        // all text on the spinner
        val adapter = ArrayAdapter(
            context,
            R.layout.select_dialog_singlechoice, arrayOf(allText)
        )
        //adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        setAdapter(adapter)
    }

    fun getSelectedItemValues():String {

        var commaSeperatedText = ""
        for (i in selectedSaved.indices) {
            if(selectedSaved[i]) {
                commaSeperatedText = commaSeperatedText.plus((this.itemValues!!.get(i) as UserChallenge).toString())
                    .plus(",")
            }
        }
        commaSeperatedText = commaSeperatedText.trim(',')

        return commaSeperatedText
    }

    interface MultiSpinnerListener {
        fun onItemsSelected(selected: BooleanArray?)
    }

    fun makeSelection(position:Int) {
        selected[position] = true
        selectedSaved[position] = true
    }
}