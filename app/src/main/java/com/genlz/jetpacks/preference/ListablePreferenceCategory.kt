package com.genlz.jetpacks.preference

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceCategory

class ListablePreferenceCategory(
    context: Context,
    attrs: AttributeSet? = null,
) : PreferenceCategory(context, attrs), Listable {

    companion object {
        private const val TAG = "ListPreferenceCategory"
    }

    override var adapter: ListAdapter<*, *>? = null
        set(value) {
            field = value
            init()
        }

    @Suppress("UNCHECKED_CAST")
    private fun init() {
        if (adapter == null) {
            Log.w(TAG, "No adapter has set.")
            return
        }
        val a = adapter as ListAdapter<Any, Preference>
        for (i in 0 until a.getItemCount()) {
            val preference = a.onCreateItem(this, a.getItemType(i))
            addPreference(preference)
        }
    }

}