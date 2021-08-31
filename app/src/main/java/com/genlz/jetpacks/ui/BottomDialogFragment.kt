package com.genlz.jetpacks.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.BottomSheetLayoutBinding
import com.genlz.jetpacks.databinding.BottomSheetPage1Binding
import com.genlz.jetpacks.databinding.BottomSheetPage2Binding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "BottomDialogFragment"

class BottomDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomSheetLayoutBinding::bind)

    private val args by navArgs<BottomDialogFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titles = args.titles

        binding.pager.adapter = BottomSheetPagerAdapter(this, titles)
        with(dialog as BottomSheetDialog) {
            behavior.halfExpandedRatio = 0.8f

        }

        TabLayoutMediator(binding.tab, binding.pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        Toast.makeText(requireContext().applicationContext, "cancel", Toast.LENGTH_SHORT).show()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        Toast.makeText(requireContext().applicationContext, "dismiss", Toast.LENGTH_SHORT).show()
    }
}

class BottomSheetPagerAdapter(
    fragment: Fragment,
    private val pageTitles: Array<String>,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = pageFactory.size

    override fun createFragment(position: Int): Fragment {
        return pageFactory[position](pageTitles[position])
    }

    companion object {
        private val pageFactory = listOf(
            BottomSheetPage1::newInstance,
            BottomSheetPage2::newInstance,
        )
    }
}

private const val PARAM_TITLE = "title"

class BottomSheetPage1 : Fragment(R.layout.bottom_sheet_page1) {

    private val binding by viewBinding(BottomSheetPage1Binding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(PARAM_TITLE)
            ?: throw IllegalArgumentException("You must pass in parameter!")
        binding.bottomSheetPage1Text.text = title
    }

    companion object {
        fun newInstance(title: String): BottomSheetPage1 {
            return BottomSheetPage1().apply {
                arguments = bundleOf(
                    PARAM_TITLE to title,
                )
            }
        }
    }
}

class BottomSheetPage2 : Fragment(R.layout.bottom_sheet_page2) {

    private val binding by viewBinding(BottomSheetPage2Binding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(PARAM_TITLE)
            ?: throw IllegalArgumentException("You must pass in parameter!")
        binding.bottomSheetPage2Text.text = title
    }

    companion object {

        fun newInstance(title: String): BottomSheetPage2 {
            return BottomSheetPage2().apply {
                arguments = bundleOf(
                    PARAM_TITLE to title
                )
            }
        }
    }
}