package com.genlz.jetpacks.preference

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.genlz.jetpacks.R
import com.genlz.share.util.launchAndRepeatWithViewLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainPreferenceFragment : PreferenceFragmentCompat() {

    private val viewModel by viewModels<MainPreferenceFragmentViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference_fragment, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference<ListablePreferenceCategory>("list_preference_category")?.apply {
            adapter = SimpleAdapter(listOf("A", "B")).apply {
                launchAndRepeatWithViewLifecycle {
                    viewModel.data.collect {
                        submitList(it)
                    }
                }
            }
        }
    }

    private fun updateData(position: Int, checked: Boolean) {
        val list = viewModel.data.value.toMutableList()
        val s = list[position]
        list[position] = if (checked) "$s$s" else s
        viewModel.update(list)
    }

    inner class SimpleAdapter(data: List<String>) : ListAdapter<String, Preference>(data) {
        override fun onCreateItem(parent: Listable, itemType: Int): Preference {
            val category = parent as PreferenceCategory
            return SwitchPreferenceCompat(category.context).apply {
                onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                    updateData(p.order, v as Boolean)
                    true
                }
            }
        }

        override fun onBindItem(item: Preference, model: String) {
            val switch = item as SwitchPreferenceCompat
            switch.title = model
        }
    }

    fun newInstance(): MainPreferenceFragment {
        val args = Bundle()

        val fragment = MainPreferenceFragment()
        fragment.arguments = args
        return fragment
    }
}

class MainPreferenceFragmentViewModel : ViewModel() {

    private val _data = MutableStateFlow(listOf("A", "B"))
    val data = _data.asStateFlow()

    fun update(data: List<String>) {
        _data.value = data
    }
}