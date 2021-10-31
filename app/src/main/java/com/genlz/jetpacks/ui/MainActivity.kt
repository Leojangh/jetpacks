package com.genlz.jetpacks.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AnticipateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.updatePadding
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
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import com.android.example.rsmigration.ImageProcessor
import com.android.example.rsmigration.VulkanImageProcessor
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.ActivityMainBinding
import com.genlz.jetpacks.ui.common.ActionBarCustomizer
import com.genlz.jetpacks.ui.common.FabSetter
import com.genlz.jetpacks.ui.common.FullscreenController
import com.genlz.jetpacks.ui.common.ReSelectable
import com.genlz.jetpacks.utility.appcompat.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    FullscreenController,
    ActionBarCustomizer,
    FabSetter {

    private val binding by viewBinding(ActivityMainBinding::class.java)

    private val viewModel by viewModels<MainActivityViewModel>()

    private val splashScreen by lazy { installSplashScreen() }

    private val windowInfoRepo by lazy { windowInfoRepository() }

    private val navController: NavController get() = navHostFragment.navController

    private val currentFragment get() = navHostFragment.childFragmentManager.fragments[0]

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.content_main) as NavHostFragment
    }

    private val windowInsetsController by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }

    private val appBarConfiguration = AppBarConfiguration(
        setOf(
            R.id.communityFragment,
            R.id.productsFragment,
            R.id.serviceFragment,
            R.id.vipFragment
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitForReady()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        customExitAnimation()
        edge2edge()

        Log.d(TAG, "onCreate: $this")

        requestPermissions()

        setupNavigation()

        listenWindowInfo()

        /**
         * Although the menu item id is as same as fragment node in navigation graph,but in fact
         * the fragment id is the fragment container's id at runtime.
         */
        binding.bottomNavigation.setOnItemReselectedListener {
            (currentFragment as? ReSelectable)?.onReselect()
        }

        var job: Job? = null
        val processor = VulkanImageProcessor(this)
        val target = binding.root
        target.post {
            processor.configureInputAndOutput(
                target.drawToBitmap(), 1
            )
        }
        binding.fab.setOnClickListener {
//            val uri = "https://baidu.com"
//            navController.navigate(WebFragmentDirections.web(uri))

            job?.cancel()
            job = lifecycleScope.launch(Dispatchers.Default) {
                val blurredBitmap = processor.blur(13f, 0)
                withContext(Dispatchers.Main) {
                    target.foreground =
                        blurredBitmap.toDrawable(resources).apply { alpha = 100 }
                }
            }
        }
    }

    private fun blur(processor: ImageProcessor, progress: Int): Bitmap {
        val radius = rescale(progress, 1.0, 25.0)
        return processor.blur(radius.toFloat(), 0)
    }

    /**
     * Helper method to map the progress from [0, 100] to the given range [min, max].
     */
    private fun rescale(progress: Int, min: Double, max: Double): Double {
        return (max - min) * (progress / 100.0) + min
    }

    override fun enterFullscreen() {
        binding.apply {
            bottomAppBar.performHide()
            appBarLayout.setExpanded(false)
        }
//        DisplayCutoutCompat()
//        windowInsetsController.run {
//            hide(WindowInsetsCompat.Type.systemBars())
//            systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
    }

    override fun exitFullscreen() {
        binding.apply {
            bottomAppBar.performShow()
            appBarLayout.setExpanded(true)
        }
//        windowInsetsController.run {
//            show(WindowInsetsCompat.Type.systemBars())
//        }
    }

    private fun listenWindowInfo() {

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
            WindowInsetsCompat.CONSUMED
        }

        // Make sure the 'content_main' is always adjacent to appbar.
        // Because update padding has no impact for it's parent,aka the view parent,
        // view group no need to requestLayout.Recursive occurs in this listener if
        // update margins.
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { abl, offset ->
            // It seems that the ABL.ScrollViewBehavior can automatically adjust paddings.
//            binding.contentMain.updatePadding(top = abl.height + offset)
            // Control status bar appearance.
            val collapse = -offset == abl.height
            windowInsetsController.isAppearanceLightStatusBars = collapse
            window.statusBarColor =
                if (collapse) getColorExt(R.color.statusBarColor) else Color.TRANSPARENT
        })

        //BottomAppBar has already fit navigation bar.
        binding.bottomNavigation.setOnApplyWindowInsetsListener { v, _, _ ->
            v.updatePadding(bottom = 0)
            WindowInsetsCompat.CONSUMED
        }
        windowInsetsController.isAppearanceLightNavigationBars = true
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        clearEditTextFocus(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun clearEditTextFocus(ev: MotionEvent) {
        val v = currentFocus
        if (ev.action == MotionEvent.ACTION_UP && R.id.edit_text == v?.id) {
            val rect = Rect()
            v.getGlobalVisibleRect(rect)
            if (!rect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                v.clearFocus()
                windowInsetsController.hide(WindowInsetsCompat.Type.ime())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        if launch mode is not standard,handle it manually like this
//        navController.handleDeepLink(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
        const val SPLASH_DISPLAY_TIME = 1000L

        init {
            System.loadLibrary("rs_migration_jni")
        }
    }

}
