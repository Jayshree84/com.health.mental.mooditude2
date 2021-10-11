package com.health.mental.mooditude.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.custom.Circle
import com.health.mental.mooditude.fragment.onboarding.SelectThemeFragment


/**
 * Created by Jayshree Rathod on 16,July,2021
 */
class CircleColorAdapter(val mContext: Context,
                         val mThemeList: Array<SelectThemeFragment.AppTheme>) : BaseAdapter() {

    override fun getCount(): Int {
        return mThemeList.size
    }

    override fun getItem(position: Int): Any {
        return mThemeList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val circle: Circle
        if (convertView == null) {
            circle = Circle(mContext!!)
            circle.layoutParams = AbsListView.LayoutParams(mContext!!.resources.getDimensionPixelSize(R.dimen._40sdp),
                mContext!!.resources.getDimensionPixelSize(R.dimen._40sdp))
        } else {
            circle = convertView as Circle
        }
        circle.setColor(ContextCompat.getColor(mContext!!, mThemeList.get(position).color))
        return circle
    }


}