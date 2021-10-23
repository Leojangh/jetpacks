package com.genlz.jetpacks.ui.community.follow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentFollowBinding
import com.genlz.jetpacks.datasource.SplashDataSource
import com.genlz.jetpacks.ui.GalleryFragment
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.community.Titleable
import com.genlz.jetpacks.ui.community.Titleable.Companion.TITLE_RES_KEY
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar

class FollowFragment : Fragment(R.layout.fragment_follow), Titleable {

    private val binding by viewBinding<FragmentFollowBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        findFabSetter()?.setupFab {
            show()
            setImageResource(R.drawable.ic_baseline_thumb_up)
            setOnClickListener {
                Snackbar.make(it, R.string.follow, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(
            @StringRes
            titleStringResId: Int = R.string.follow
        ) = FollowFragment().apply {
            arguments = bundleOf(TITLE_RES_KEY to titleStringResId)
        }
    }

    override val titleStringResId: Int
        get() = arguments?.getInt(TITLE_RES_KEY)
            ?: error("Not title resource passed in as parameter")
}

private const val TAG = "FollowFragment"