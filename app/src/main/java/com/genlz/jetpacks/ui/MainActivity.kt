package com.genlz.jetpacks.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoRepository
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModels<MainActivityViewModel>()

    private lateinit var splashScreen: SplashScreen

    private lateinit var windowInfoRepo: WindowInfoRepository

    /**
     * 未适配连续性时，Fold折叠后会销毁并重新创建一个Activity，再次打开又会创建一个Activity。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = installSplashScreen()
        waitForReady()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        customExitAnimation()
        edge2edge()

        Log.d(TAG, "onCreate: $this")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions()
        }
        setupNavigation()
        windowInfoRepo = windowInfoRepository()

        listenWindowInfo()
    }

    private fun listenWindowInfo() {
        val STAG = "slfsjfksjfk"

        lifecycleScope.launch {
            windowInfoRepo.currentWindowMetrics.flowWithLifecycle(lifecycle)
                .collect {
                    Log.d(STAG, "onCreate:width ${it.bounds.width()} height${it.bounds.height()}")
                }
        }

        //WindowManager 会在应用跨屏显示（以物理或虚拟方式）时提供 LayoutInfo 数据（设备功能类型、设备功能边界和设备折叠状态）。
        // 因此，在上图中，应用在单屏模式下运行时，WindowLayoutInfo 为空。
        lifecycleScope.launch {
            windowInfoRepo.windowLayoutInfo.flowWithLifecycle(lifecycle).collect {
                Log.d(STAG, "onCreate: No display features detected")
                for (displayFeature: DisplayFeature in it.displayFeatures) {
                    if (displayFeature is FoldingFeature && displayFeature.occlusionType == FoldingFeature.OcclusionType.NONE) {
                        Log.d(STAG, "onCreate: App is spanned across a fold")
                    }
                    if (displayFeature is FoldingFeature && displayFeature.occlusionType == FoldingFeature.OcclusionType.FULL) {
                        Log.d(STAG, "onCreate: App is spanned across a hinge")
                    }
                }
            }
        }
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
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.vipFragment,
            R.id.serviceFragment,
            R.id.productsFragment,
            R.id.communityFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
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

    //TODO fix not working
    private fun edge2edge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, i ->
            val insets = i.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = 1000)
            i
        }
        val initialMarginBottom = binding.bottomNavigation.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, i ->
            val insets = i.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(bottom = insets.bottom + initialMarginBottom)
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

    /**
     * MultiResume
     */
    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        super.onTopResumedActivityChanged(isTopResumedActivity)
        if (isTopResumedActivity) {
            Log.d(TAG, "onTopResumedActivityChanged: top resume")
        } else {
            Log.d(TAG, "onTopResumedActivityChanged: no longer top resume")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        if launch mode is not standard,handle it manually like this
//        navController.handleDeepLink(intent)
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