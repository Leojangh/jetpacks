package com.genlz.jetpacks.ui.service

import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import androidx.constraintlayout.helper.widget.Carousel
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentServiceBinding
import com.genlz.jetpacks.databinding.GalleryBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.internal.CollapsingTextHelper

class ServiceFragment : Fragment(R.layout.gallery) {

    private val binding by viewBinding(GalleryBinding::bind)

    private val viewModel by viewModels<ServiceFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarousel()
    }

    private fun setupCarousel() {
        val colors = intArrayOf(
            Color.parseColor("#ffd54f"),
            Color.parseColor("#ffca28"),
            Color.parseColor("#ffc107"),
            Color.parseColor("#ffb300"),
            Color.parseColor("#ffa000"),
            Color.parseColor("#ff8f00"),
            Color.parseColor("#ff6f00"),
            Color.parseColor("#c43e00")
        )
        val carousel = binding.carousel
        val numImages = colors.size
        carousel.setAdapter(object : Carousel.Adapter {
            override fun count(): Int {
                return numImages
            }

            override fun populate(view: View, index: Int) {
                if (view is MaterialCardView) {
                    view.setBackgroundColor(colors[index])
                }
            }

            override fun onNewItem(index: Int) {
            }
        })
    }

    companion object {
        private const val TAG = "ServiceFragment"
    }
}