package com.genlz.jetpacks.utility

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @param context Application context
 */
class UiModeInspector private constructor(
    context: Context,
) {
    init {
        if (context !is Application) throw IllegalArgumentException("$context is not a application context")

        context.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                getInstance().parseNewConfiguration(newConfig)
            }

            override fun onLowMemory() {
                //Don't care.
            }
        })
    }

    private val _darkModeLiveData = MutableLiveData(context.isDarkMode)
    val darkModeLiveData: LiveData<Boolean> = _darkModeLiveData

    private fun parseNewConfiguration(newConfig: Configuration) {
        _darkModeLiveData.value = newConfig.isDarkModeActive
    }

    companion object {

        val Context.isDarkMode
            get() = resources.configuration.isDarkModeActive

        /**
         * Platform R has implemented already.
         */
        val Configuration.isDarkModeActive
            get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        @Volatile
        @JvmStatic
        private var INSTANCE: UiModeInspector? = null

        /**
         * Call this at [Application.onCreate].Alternatively use androidx startup lib to init it.
         * Nothing happens if invoke again.
         */
        @JvmStatic
        fun init(context: Context) {
            if (INSTANCE == null) {
                synchronized(UiModeInspector::class.java) {
                    if (INSTANCE == null)
                        INSTANCE = UiModeInspector(context)
                }
            }
        }

        @JvmStatic
        fun getInstance(): UiModeInspector {
            return INSTANCE ?: error("Not initialize yet!")
        }

        @Suppress("UNUSED")
        private const val TAG = "UiModeInspector"
    }
}
