package com.health.mental.mooditude.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.health.mental.mooditude.R


/**
 * Created by Jayshree Rathod on 15,July,2021
 */
object AnimationUtils {
    fun onFlipAniPerform(llFlipView: View, txtDesc: View, isAnimation: Boolean) {
        if(isAnimation){

            val oa1 = ObjectAnimator.ofFloat(llFlipView, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(llFlipView, "scaleX", 0f, 1f)
            oa1.interpolator = DecelerateInterpolator()
            oa1.duration=100
            oa2.duration=200
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    //      lblAge18Desc.visibility = View.VISIBLE
                    //llAge18.setImageResource(R.drawable.frontSide)
                    oa2.start()
                }
            })
            oa1.start()
            oa2.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    var visibility= View.GONE
                    when(txtDesc!!.visibility) {
                        View.VISIBLE->{
                            visibility= View.GONE
                            llFlipView!!.setBackgroundResource(0)
                        }
                        View.GONE->{
                            llFlipView!!.setBackgroundResource(R.drawable.option_desc_bg)
                            visibility= View.VISIBLE
                        }
                    }
                    txtDesc!!.visibility=visibility


                }
            })

        }
        else
        {
            llFlipView!!.setBackgroundResource(0)
            txtDesc!!.visibility= View.GONE
        }
    }

    fun flip(front: View, back: View, duration: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val set = AnimatorSet()
            set.playSequentially(
                ObjectAnimator.ofFloat(front, "rotationY", 90f)
                    .setDuration((duration / 2).toLong()),
                ObjectAnimator.ofInt(front, "visibility", View.GONE).setDuration(0),
                ObjectAnimator.ofFloat(back, "rotationY", -90f).setDuration(0),
                ObjectAnimator.ofInt(back, "visibility", View.VISIBLE).setDuration(0),
                ObjectAnimator.ofFloat(back, "rotationY", 0f).setDuration((duration / 2).toLong())
            )
            set.start()
        } else {
            front.animate().rotationY(90f).setDuration((duration / 2).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        front.visibility = View.GONE
                        back.rotationY = -90f
                        back.visibility = View.VISIBLE
                        back.animate().rotationY(0f).setDuration((duration / 2).toLong())
                            .setListener(null)
                    }
                })
        }
    }
}