package com.health.mental.mooditude.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.listener.ActivityIconSelectListener


/**
 * Created by Jayshree Rathod on 18,August,2021
 */
class UserActivityIconAdapter(context: Context, val dataList: Array<String>, val listener: ActivityIconSelectListener)
    : ArrayAdapter<String>(context, 0 , dataList) {

    private var lastSelectedPosition = -1

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): String {
        return dataList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position1: Int, convertView: View?, parent: ViewGroup): View {

        var listitemView = convertView
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(context).inflate(R.layout.situation, parent, false)
        }
        listitemView!!.tag = position1
        val imageName = getItem(position1)
        val textView1 = listitemView!!.findViewById<TextView>(R.id.tv_text1)
        val textView2 = listitemView.findViewById<TextView>(R.id.tv_text2)
        val ivSelection = listitemView.findViewById<ImageView>(R.id.iv_selection)
        textView2.visibility = View.GONE

        val secondaryColor = ContextCompat.getColor(context, R.color.secondaryColor)
        val primaryColor = ContextCompat.getColor(context, R.color.primaryColor)

        val isChecked = lastSelectedPosition == position1

        if(isChecked) {
            textView1.setTextColor(primaryColor)
            ivSelection.visibility = View.VISIBLE
        }
        else {
            textView1.setTextColor(secondaryColor)
            ivSelection.visibility = View.INVISIBLE
        }

        textView1.setText(imageName)

        listitemView.setOnClickListener {

            val position = it.tag as Int
            if(lastSelectedPosition != position) {
                lastSelectedPosition = position
                notifyDataSetChanged()

                //call listener to update next button
                listener.onIconSelected(this, dataList.get(lastSelectedPosition))
            }

            //To redraw view
            notifyDataSetInvalidated()
        }

        return listitemView
    }

    fun removeSelection() {
        lastSelectedPosition = -1
        notifyDataSetChanged()
    }


}