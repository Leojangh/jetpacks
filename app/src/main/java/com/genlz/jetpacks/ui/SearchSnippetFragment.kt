package com.genlz.jetpacks.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentSearchSnippetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchSnippetFragment : Fragment(R.layout.fragment_search_snippet) {

    private val viewModel by viewModels<SearchSnippetViewModel>()
    private val binding by viewBinding<FragmentSearchSnippetBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        const val TAG = "SearchSnippetFragment"
    }
}