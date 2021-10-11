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


/**
 * Created by Jayshree Rathod on 18,August,2021
 */
class UserActivityApiAdapter(context: Context, val dataList: ArrayList<UserActivity>)
    : ArrayAdapter<UserActivity>(context, 0 , dataList) {


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

        val primaryColor = ContextCompat.getColor(context, R.color.primaryColor)
        textView1.setTextColor(primaryColor)
        textView2.setTextColor(primaryColor)
        ivSelection.visibility = View.INVISIBLE

        textView1.setText(userActivity.imageName)
        textView2.setText(userActivity.title)



        return listitemView
    }


}