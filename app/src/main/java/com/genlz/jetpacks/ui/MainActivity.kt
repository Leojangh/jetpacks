package com.genlz.jetpacks.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.*
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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
import com.genlz.jetpacks.ui.common.ActionBarCustomizer
import com.genlz.jetpacks.ui.common.FabSetter
import com.genlz.jetpacks.ui.common.FullscreenController
import com.genlz.jetpacks.utility.appcompat.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    FullscreenController,
    ActionBarCustomizer,
    FabSetter {

    private val binding by viewBinding(ActivityMainBinding::class.java)

    private val viewModel by viewModels<MainActivityViewModel>()

    private lateinit var splashScreen: SplashScreen

    private lateinit var windowInfoRepo: WindowInfoRepository

    private lateinit var navController: NavController

    private val appBarConfiguration = AppBarConfiguration(
        setOf(
            R.id.communityFragment,
            R.id.productsFragment,
            R.id.serviceFragment,
            R.id.vipFragment
        )
    )

    /**
     * 未适配连续性时，Fold折叠后会销毁并重新创建一个Activity，再次打开又会创建一个Activity。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = installSplashScreen()
        waitForReady()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.content_main) as NavHostFragment
        navController = navHostFragment.navController

        customExitAnimation()
        edge2edge()

        Log.d(TAG, "onCreate: $this")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions()
        }
        setupNavigation()
        windowInfoRepo = windowInfoRepository()

        listenWindowInfo()

        binding.bottomNavigation.getOrCreateBadge(R.id.communityFragment).apply {
            number = 20
        }
    }

    override fun enterFullscreen() {
        binding.apply {
            bottomAppBar.performHide()
//            appBarLayout.animate().translationY(-appBarLayout.height.toFloat()).start()

//            appBarLayout.isLifted = true
        }

//        WindowInsetsControllerCompat(window, binding.root).run {
//            hide(WindowInsetsCompat.Type.systemBars())
//            systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
    }

    override fun exitFullscreen() {
        binding.apply {
            bottomAppBar.performShow()
//            appBarLayout.animate().translationY(appBarLayout.height.toFloat()).start()
//            appBarLayout.isLifted = false
        }
//        WindowInsetsControllerCompat(window, binding.root).run {
//            show(WindowInsetsCompat.Type.systemBars())
//        }
    }

    private fun listenWindowInfo() {

        lifecycleScope.launch {
            windowInfoRepo.currentWindowMetrics.flowWithLifecycle(lifecycle)
                .collect {
                    Log.d(TAG, "onCreate:width ${it.bounds.width()} height${it.bounds.height()}")
                }
        }

        //WindowManager 会在应用跨屏显示（以物理或虚拟方式）时提供 LayoutInfo 数据（设备功能类型、设备功能边界和设备折叠状态）。
        // 因此，在上图中，应用在单屏模式下运行时，WindowLayoutInfo 为空。
        lifecycleScope.launch {
            windowInfoRepo.windowLayoutInfo.flowWithLifecycle(lifecycle).collect {
                Log.d(TAG, "onCreate: No display features detected")
                for (displayFeature: DisplayFeature in it.displayFeatures) {
                    if (displayFeature is FoldingFeature && displayFeature.occlusionType == FoldingFeature.OcclusionType.NONE) {
                        Log.d(TAG, "onCreate: App is spanned across a fold")
                    }
                    if (displayFeature is FoldingFeature && displayFeature.occlusionType == FoldingFeature.OcclusionType.FULL) {
                        Log.d(TAG, "onCreate: App is spanned across a hinge")
                    }
                }
            }
        }
    }

    override fun custom(customizer: ActionBar.() -> Unit) {
        with(supportActionBar ?: error("No action bar supplied!"), customizer)
    }

    override fun setupFab(action: FloatingActionButton.() -> Unit) {
        action(binding.fab)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    //perform action
                    Log.d(TAG, "requestPermissions: perform")
                } else {
                    Log.d(TAG, "requestPermissions: denied")
                }
            }

        when {
            checkSelfPermissionExt(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "requestPermissions: perform")
            }
            shouldShowRequestPermissionRationaleExt(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d(TAG, "requestPermissions: show rationale")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                Log.d(TAG, "requestPermissions: request")
            }
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun waitForReady() {
        splashScreen.setKeepVisibleCondition {
            !viewModel.ready.value
        }
    }

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

    /**
     * The attribute fitSystemWindow has some bug or little glitch such as no effect with
     * bottom navigation view;app bar layout can't lift thoroughly.
     */
    private fun edge2edge() {
        window.setDecorFitsSystemWindowsExt(false)
        binding.toolbarLayout.setOnApplyWindowInsetsListener { v, i, ip ->
            v.updatePadding(top = i.statusBarInsets.top + ip.top)
            //Adjust marginTop after measured.
            v.post {
                binding.contentMain.updateMargin(top = v.height)
            }
            WindowInsetsCompat.CONSUMED
        }

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.contentMain.updateMargin(top = appBarLayout.height + verticalOffset)
            // Control status bar appearance.
            val fullscreen = binding.contentMain.marginTop == 0
            controller.isAppearanceLightStatusBars = fullscreen
            window.statusBarColor =
                if (fullscreen) getColorExt(R.color.statusBarColor) else Color.TRANSPARENT
        })

        //BottomAppBar has already fit navigation bar.
        binding.bottomNavigation.setOnApplyWindowInsetsListener { v, _, _ ->
            v.updatePadding(bottom = 0)
            WindowInsetsCompat.CONSUMED
        }

    }

    /**
     * 提供向上返回支持
     */
    override fun onSupportNavigateUp(): Boolean {
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

    companion object {
        private const val TAG = "MainActivity"
        const val SPLASH_DISPLAY_TIME = 1000L

    }

}
