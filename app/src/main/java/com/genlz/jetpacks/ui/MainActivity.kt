package com.genlz.jetpacks.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.*
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModels<MainActivityViewModel>()

    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = installSplashScreen()
        waitForReady()
        setContentView(binding.root)
        customExitAnimation()
        edge2edge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions()
        }

        setupNavigation()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    //perform action
                    Log.d(TAG, "requestPermissions: perform")
                } else {
                    Log.d(TAG, "requestPermissions: denied")
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "requestPermissions: perform")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d(TAG, "requestPermissions: show rationale")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                Log.d(TAG, "requestPermissions: request")
            }
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun waitForReady() {
        splashScreen.setKeepVisibleCondition {
            !viewModel.ready.value
        }
    }

    private fun customExitAnimation() {
        splashScreen.setOnExitAnimationListener { provider ->
            val splashScreenView = provider.view

            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            ).apply {
                interpolator = AnticipateInterpolator()
                duration = 300L
                doOnEnd { provider.remove() }
            }
            slideUp.start()
        }
    }

    private fun edge2edge() {
        setSupportActionBar(binding.toolbar)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, i ->
            val insets = i.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }
        val initialMarginBottom = binding.fab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.fab) { v, i ->
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin =
                    i.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom + initialMarginBottom
            }
            i
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun hideSystemBars() {
        val controllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        controllerCompat.hide(WindowInsetsCompat.Type.systemBars())
        controllerCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    companion object {
        private const val TAG = "MainActivity"
        const val SPLASH_DISPLAY_TIME = 1000L
    }
}