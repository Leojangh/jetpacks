package com.genlz.jetpacks.ui

import android.os.Bundle
import android.util.Log
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.genlz.jetpacks.threadaffinity.CpuFrequencyMonitor
import com.genlz.share.util.appcompat.animateExt
import com.genlz.share.util.appcompat.doOnEnd
import com.genlz.share.util.appcompat.lazyNoneSafe
import com.genlz.share.util.appcompat.setDecorFitsSystemWindowsExt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.io.path.Path
import kotlin.io.path.readText

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    private val splashScreen by lazyNoneSafe { installSplashScreen() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitForReady()
        customExitAnimation()

        window.setDecorFitsSystemWindowsExt(false)
        setContent {
            ComposeApp()
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            }
        }
    }

    /**
     * Wait for the first screen ready,set condition to keep splash screen visible.
     * Must be invoked before [setContentView].
     */
    private fun waitForReady() {
        splashScreen.setKeepOnScreenCondition {
            !viewModel.ready.value
        }
    }

    /**
     * Custom the splash screen exit animation at here.
     */
    private fun customExitAnimation() {
        splashScreen.setOnExitAnimationListener { provider ->
            provider.view.apply {
                animateExt().apply {
                    translationY(-height.toFloat())
                    interpolator = AnticipateInterpolator()
                    duration = 300L
                    doOnEnd { provider.remove() }
                }.start()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        const val SPLASH_DISPLAY_TIME = 1000L
    }

}
