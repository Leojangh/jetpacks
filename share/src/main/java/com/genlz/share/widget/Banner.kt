package com.genlz.share.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Checkable
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.genlz.share.R
import com.genlz.share.databinding.BannerBinding
import java.util.*

class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var period: Long

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Banner,
            defStyleAttr,
            defStyleRes
        ).run {
            try {
                period = getInteger(R.styleable.Banner_playback_period, PERIOD.toInt()).toLong()
            } finally {
                recycle()
            }
        }
    }

    private val binding: BannerBinding =
        BannerBinding.inflate(LayoutInflater.from(context), this, true)

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        binding.apply {
            repeat(adapter.itemCount) {
                inflate(context, R.layout.simple_radio_button, indicator)
            }
            poster.adapter = adapter
            poster.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (indicator[position] as Checkable).isChecked = true
                }
            })
            Timer().schedule(object : TimerTask() {
                private var index = 0
                override fun run() {
                    poster.currentItem = ++index % adapter.itemCount
                }
            }, period, period)
        }
    }

    companion object {
        private const val TAG = "Banner"
        const val PERIOD = 2000L
    }
}