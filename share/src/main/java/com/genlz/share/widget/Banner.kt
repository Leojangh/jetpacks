package com.genlz.share.widget

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.Checkable
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.genlz.share.R
import com.genlz.share.databinding.BannerBinding
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.*
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = BannerBinding.inflate(LayoutInflater.from(context), this)

    var period: Long = DEFAULT_PERIOD

    var showIndicator: Boolean = true
        set(value) {
            field = value
            binding.indicator.visibility = if (value) VISIBLE else GONE
        }

    var autoPlay by object : ObservableProperty<Boolean>(true) {

        override fun afterChange(
            property: KProperty<*>,
            oldValue: Boolean,
            newValue: Boolean
        ) {
            if (itemCount < 2) return //no need playback
            if (newValue) {
                autoPlayJob =
                    CoroutineScope(Dispatchers.Main.immediate).launch {
                        while (true) {
                            delay(period)
                            binding.poster.currentItem =
                                (binding.poster.currentItem + 1) % itemCount
                        }
                    }
            } else {
                autoPlayJob?.cancel()
            }
        }
    }

    init {
//        initView(context)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Banner,
            defStyleAttr,
            defStyleRes
        ).run {
            period = getInteger(R.styleable.Banner_playback_period, DEFAULT_PERIOD.toInt()).toLong()
            if (!getBoolean(R.styleable.Banner_autoPlay, true)) {
                autoPlay = false
            }
            showIndicator = getBoolean(R.styleable.Banner_showIndicator, true)
            recycle()
        }
    }

    /**
     * Layout Inspector won't show view hierarchy.
     */
    private fun initView(context: Context) {
        addView(ViewPager2(context), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(
            RadioGroup(context).apply {
                orientation = LinearLayout.HORIZONTAL
            },
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = 8.toDp(resources.displayMetrics)
            }
        )
    }

    val itemCount get() = binding.poster.adapter?.itemCount ?: 0

    private var autoPlayJob: Job? = null

    private fun initIndicator() {
        if (itemCount > 1) {
            repeat(itemCount) {
                MaterialRadioButton(context).apply {
                    gravity = Gravity.CENTER
                    isClickable = false
                    buttonDrawable = context.theme.getDrawable(R.drawable.pager_indicator)
                    val dm = resources.displayMetrics
                    val size = 5.toDp(dm)
                    val margin = 4.toDp(dm)
                    layoutParams = MarginLayoutParams(size, size).apply {
                        bottomMargin = margin
                        topMargin = margin
                        leftMargin = margin
                        rightMargin = margin
                    }
                }.also {
                    binding.indicator.addView(it)
                }
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
            (this[0] as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        binding.poster.adapter = adapter
        initPager()
        initIndicator()
        activate()
    }

    /**
     * Call this method to ensure auto play job works well.
     */
    private fun activate() {
        autoPlay = autoPlay
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_UP -> activate()

            else -> autoPlayJob?.cancel()
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        private const val TAG = "Banner"
        const val DEFAULT_PERIOD = 2000L
    }
}