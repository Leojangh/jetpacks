package com.genlz.share.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.Checkable
import android.widget.EdgeEffect
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.genlz.share.R
import com.genlz.share.databinding.BannerBinding
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: BannerBinding = BannerBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val gestureDetector =
        GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return super.onFling(e1, e2, velocityX, velocityY)
            }

            override fun onLongPress(e: MotionEvent?) {
                Log.d(TAG, "onLongPress: ")
            }

            //always return true unless need to ignore all gestures truly.
            override fun onDown(e: MotionEvent?) = true
        })

    var period: Long

    var autoPlay by object : ObservableProperty<Boolean>(true) {

        override fun afterChange(
            property: KProperty<*>,
            oldValue: Boolean,
            newValue: Boolean
        ) {
            if (itemCount < 2) return //no need playback
            if (newValue) {
                autoPlayJob?.cancel()
                autoPlayJob = coroutineScope.launch {
                    autoPlay()
                }
            } else {
                autoPlayJob?.cancel()
            }
        }
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Banner,
            defStyleAttr,
            defStyleRes
        ).run {
            try {
                period = getInteger(R.styleable.Banner_playback_period, PERIOD.toInt()).toLong()
                if (!getBoolean(R.styleable.Banner_autoPlay, true)) {
                    autoPlay = false
                }
            } finally {
                recycle()
            }
        }
    }

    private suspend fun autoPlay() {
        while (true) {
            delay(period)
            pageSwitcher.run()
        }
    }

    private val pageSwitcher = object : Runnable {

        private var index = 0

        override fun run() {
            binding.poster.currentItem = ++index % itemCount
        }
    }

    val itemCount get() = binding.poster.adapter?.itemCount ?: 0

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private var autoPlayJob: Job? = null

    private fun initIndicator() {
        if (itemCount > 1) {
            repeat(itemCount) {
                inflate(context, R.layout.simple_radio_button, binding.indicator)
            }
        }
    }

    private fun initPager() {
        binding.poster.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (binding.indicator[position] as Checkable).isChecked = true
                }
            })
        }
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        binding.poster.adapter = adapter
        initPager()
        initIndicator()
        autoPlay = autoPlay //activate
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent: $event")
        return gestureDetector.onTouchEvent(event)
    }

//    override fun performClick(): Boolean {
//        return super.performClick()
//    }

    companion object {
        private const val TAG = "Banner"
        const val PERIOD = 2000L
    }
}