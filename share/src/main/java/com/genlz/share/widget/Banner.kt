package com.genlz.share.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.genlz.share.R

class Banner(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawable: Drawable?

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 50f
        color = Color.BLACK
    }

    init {
        //MiuiTypeArray not support try-with-resource
        context.theme.obtainStyledAttributes(attrs, R.styleable.Banner, 0, 0).let {
            try {
                isClickable = it.getBoolean(R.styleable.Banner_android_clickable, true)
                drawable = it.getDrawable(R.styleable.Banner_android_drawable)
            } finally {
                it.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure: $widthMeasureSpec,$heightMeasureSpec")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText("banner1", width / 2f, height / 2f, paint)
    }

    companion object {
        private const val TAG = "Banner"
    }
}