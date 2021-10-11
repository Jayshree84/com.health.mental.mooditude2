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
import com.health.mental.mooditude.data.entity.UserActivity
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Jayshree Rathod on 18,August,2021
 */
class UserActivityAdapter(context: Context, val dataList: ArrayList<UserActivity>, val listener: OnActivitySelectionListener)
    : ArrayAdapter<UserActivity>(context, 0 , dataList) {

    interface OnActivitySelectionListener {
        fun onActivityTapped()
    }
    private val listSelectedPositions = arrayListOf<Int>()

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): UserActivity {
        return dataList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listitemView = convertView
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(context).inflate(R.layout.situation, parent, false)
        }
        val userActivity = getItem(position)
        val textView1 = listitemView!!.findViewById<TextView>(R.id.tv_text1)
        val textView2 = listitemView.findViewById<TextView>(R.id.tv_text2)
        val ivSelection = listitemView.findViewById<ImageView>(R.id.iv_selection)

        val secondaryColor = ContextCompat.getColor(context, R.color.secondaryColor)
        val primaryColor = ContextCompat.getColor(context, R.color.primaryColor)

        val isChecked = listSelectedPositions.contains(position)

        if(isChecked) {
            textView1.setTextColor(primaryColor)
            textView2.setTextColor(primaryColor)
            ivSelection.visibility = View.VISIBLE
        }
        else {
            textView1.setTextColor(secondaryColor)
            textView2.setTextColor(secondaryColor)
            ivSelection.visibility = View.INVISIBLE
        }

        textView1.setText(userActivity.imageName)
        textView2.setText(userActivity.title)

        listitemView.setOnClickListener {

            if(listSelectedPositions.contains(position)) {
                //already checked
                listSelectedPositions.remove(position)
            }
            else {
                listSelectedPositions.add(position)
            }
            //To redraw view
            notifyDataSetChanged()
            listener.onActivityTapped()
        }

        return listitemView
    }

    fun getSelectedActivities():ArrayList<UserActivity> {
        val listSelection = ArrayList<UserActivity>()
        for(itemPosition in listSelectedPositions) {
            listSelection.add(dataList.get(itemPosition))
        }
        return listSelection
    }

    fun setSelectedActivities(listSelection: ArrayList<UserActivity>): ArrayList<UserActivity> {
        val listToRemove:ArrayList<UserActivity> = ArrayList()
        for(activity in listSelection) {
            val index = dataList.indexOfFirst { it.activityId.equals(activity.activityId) }
            if(index != -1) {
                listSelectedPositions.add(index)
                listToRemove.add(activity)
            }
        }
        return listToRemove
    }
}