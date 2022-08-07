package com.genlz.jetpacks.ui

import android.os.Bundle
import android.util.Log
import android.view.animation.AnticipateInterpolator
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.genlz.jetpacks.ui.components.CommunityScreenViewModel
import com.genlz.share.util.appcompat.animateExt
import com.genlz.share.util.appcompat.doOnEnd
import com.genlz.share.util.appcompat.lazyNoneSafe
import com.genlz.share.util.appcompat.setDecorFitsSystemWindowsExt
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()
    private val vm by viewModels<CommunityScreenViewModel>()

    private val splashScreen by lazyNoneSafe { installSplashScreen() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitForReady()
        customExitAnimation()

        window.setDecorFitsSystemWindowsExt(false)

        setContent {
            ComposeApp(viewModel,vm)
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
