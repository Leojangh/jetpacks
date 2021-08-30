package com.genlz.jetpacks.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.BottomSheetLayoutBinding
import com.genlz.jetpacks.databinding.BottomSheetPage1Binding
import com.genlz.jetpacks.databinding.BottomSheetPage2Binding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomSheetLayoutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pager.adapter = BottomSheetPagerAdapter(this)
        with(dialog as BottomSheetDialog) {
            behavior.halfExpandedRatio = 0.8f
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        Toast.makeText(requireContext().applicationContext, "cancel", Toast.LENGTH_SHORT).show()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        Toast.makeText(requireContext().applicationContext, "dismiss", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "BottomDialogFragment"
    }
}

class BottomSheetPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = pageFactory.size

    override fun createFragment(position: Int): Fragment {
        return pageFactory[position]()
    }

    companion object {
        private val pageFactory = listOf(
            {
                BottomSheetPage1().apply {
                    arguments = bundleOf()
                }
            },
            {
                BottomSheetPage2().apply {
                    arguments = bundleOf()
                }
            }
        )
    }
}

class BottomSheetPage1 : Fragment(R.layout.bottom_sheet_page1) {
    private val binding by viewBinding(BottomSheetPage1Binding::bind)

}

class BottomSheetPage2 : Fragment(R.layout.bottom_sheet_page2) {

    private val binding by viewBinding(BottomSheetPage2Binding::bind)

}