package com.health.mental.mooditude.custom

/**
 * Created by Jayshree.Rathod on 04-10-2017.
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.health.mental.mooditude.R


class Circle @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    internal lateinit var p: Paint
    internal var color: Int = 0

    init {
        // real work here
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.Circle,
                0, 0
        )

        try {

            color = a.getColor(R.styleable.Circle_circleColor, 0xff000000.toInt())
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle()
        }
        init()
    }

    fun init() {
        p = Paint()
        p.color = color
    }

    fun setColor(color:Int) {
        this.color = color
        init()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle((height / 2).toFloat(), (width / 2).toFloat(), (width / 2).toFloat(), p)
    }

}