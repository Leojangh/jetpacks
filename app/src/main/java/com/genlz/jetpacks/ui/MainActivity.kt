package com.genlz.jetpacks.ui

import android.Manifest
import android.content.ComponentCallbacks2
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AnticipateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.ActivityMainBinding
import com.genlz.jetpacks.ui.common.ActionBarCustomizer
import com.genlz.jetpacks.ui.common.FabSetter
import com.genlz.jetpacks.ui.common.FullscreenController
import com.genlz.jetpacks.ui.common.ReSelectable
import com.genlz.jetpacks.ui.web.WebFragmentDirections
import com.genlz.share.util.appcompat.*
import com.google.android.material.appbar.AppBarLayout
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

    /**
     * Use this constructor to show top level destinations.
     */
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
        doWithPermission(Manifest.permission.READ_CALL_LOG)
        setupNavigation()
        listenWindowInfo()
        setupViews()
    }

    /**
     * Setup views at here,such as setting listeners,adjusting layout params programmatically etc.
     */
    private fun setupViews() {
        // Make sure the 'content_main' is always adjacent to appbar.
        // Because update padding has no impact for it's parent,aka the view parent,
        // view group no need to requestLayout.Recursive occurs in this listener if
        // update margins.
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { abl, offset ->
            // It seems that the ABL.ScrollViewBehavior can automatically adjust paddings.
            // Control status bar appearance.
            val collapse = -offset == abl.height
            windowInsetsController.isAppearanceLightStatusBars = collapse
            window.statusBarColor =
                if (collapse) getColorExt(R.color.statusBarColor) else Color.TRANSPARENT
            windowInsetsController.isAppearanceLightNavigationBars = !collapse
        })

        /**
         * Although the menu item id is as same as fragment node in navigation graph,but in fact
         * the fragment id is the fragment container's id at runtime.
         */
        binding.bottomNavigation.setOnItemReselectedListener {
            (currentFragment as? ReSelectable)?.onReselect()
        }

        binding.fab.setOnClickListener {
            val uri =
                "https://mp.weixin.qq.com/s?__biz=Mzk0NDIwMTExNw==&mid=2247502282&idx=1&sn=ed9fe9ab8c3f03499e4a6766e53287de&chksm=c32ac538f45d4c2e2803933233dc191a8216914c462aecf817fe7c7d5e9b88a5835d7b32c64b&scene=0&subscene=91&sessionid=1636875512&clicktime=1636875524&enterid=1636875524&ascene=7&devicetype=android-30&version=2800103b&nettype=WIFI&abtest_cookie=AAACAA%3D%3D&lang=zh_CN&exportkey=A2m99ii%2FhukReN80W6g3A6M%3D&pass_ticket=UrTCnLOw4r%2BY093kmNUyx4K49UfzdEUnzd%2BUgbJ8sX0pWLcZu3aTHX5Wgn2UcdLP&wx_header=1"
            navController.navigate(
                WebFragmentDirections.web(uri),
            )
        }
        edge2edge()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
                Log.d(TAG, "onTrimMemory: TRIM_MEMORY_UI_HIDDEN")
            }

            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
                Log.d(
                    TAG,
                    "onTrimMemory: TRIM_MEMORY_RUNNING_MODERATE,TRIM_MEMORY_RUNNING_LOW,TRIM_MEMORY_RUNNING_CRITICAL:$level"
                )
            }

            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
                Log.d(
                    TAG,
                    "onTrimMemory: TRIM_MEMORY_COMPLETE,TRIM_MEMORY_MODERATE,TRIM_MEMORY_BACKGROUND:$level"
                )
            }

            else -> {
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                Log.d(TAG, "onTrimMemory: ")
            }
        }
    }

    override fun enterFullscreen(sticky: Boolean) {
        binding.apply {
            bottomAppBar.performHide()
            appBarLayout.setExpanded(false)
        }
//        DisplayCutoutCompat()
        //Works bad on notches.
        windowInsetsController.apply {
            hide(Type.systemBars() or Type.ime())
            systemBarsBehavior =
                if (sticky) WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                else WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        }
    }

    override fun exitFullscreen() {
        binding.apply {
            bottomAppBar.performShow()
            appBarLayout.setExpanded(true)
        }
        windowInsetsController.apply {
            show(Type.systemBars())
        }
    }

    /**
     * Jetpack Window Manager utils.
     */
    private fun listenWindowInfo() {

        //WindowManager 会在应用跨屏显示（以物理或虚拟方式）时提供 LayoutInfo 数据（设备功能类型、设备功能边界和设备折叠状态）。
        // 因此，在上图中，应用在单屏模式下运行时，WindowLayoutInfo 为空。
        lifecycleScope.launch {
            windowInfoRepo.windowLayoutInfo.flowWithLifecycle(lifecycle).collect {
                Log.d(TAG, "onCreate: No display features detected")
                for (displayFeature in it.displayFeatures) {
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

    /**
     * Bind navigation controller with bottom navigation view and action bar.
     * So we don't need to implement bottom navigation view's item click listener
     * and the action bar can show appropriate title automatically.
     */
    private fun setupNavigation() {
        binding.bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Wait for the first screen ready,set condition to keep splash screen visible.
     * Must be invoked before [setContentView].
     */
    private fun waitForReady() {
        splashScreen.setKeepVisibleCondition {
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

    /**
     * Implements edge to edge with 3 steps:
     * 1. Set system bar color to transparent.At here we didn't do it because it's done in theme.
     * 2. Laid out behind system bar,just call [setDecorFitsSystemWindowsExt].In the layout XML,
     * [android.R.attr.fitsSystemWindows] is unnecessary.
     * 3. Handles visual overlaps and gesture conflicts.The key step is listen the system window insets.
     *
     * More Reference:
     * [Android Developers Guide](https://developer.android.com/training/gestures/edge-to-edge)
     *
     * Get Chris Banes's blog for more information:
     *
     * * [Gesture Navigation: going edge-to-edge](https://medium.com/androiddevelopers/gesture-navigation-going-edge-to-edge-812f62e4e83e)
     *
     * * [WindowInsets — listeners to layouts](https://medium.com/androiddevelopers/windowinsets-listeners-to-layouts-8f9ccc8fa4d1)
     *
     * */
    private fun edge2edge() {
        window.setDecorFitsSystemWindowsExt(false)
        //fit status bar
        binding.toolbarLayout.setOnApplyWindowInsetsListener { v, i, ip ->
            v.updatePadding(top = i.statusBarInsets.top + ip.top)
            i
        }
        //fit navigation bar and ime.
        binding.bottomAppBar.setOnApplyWindowInsetsListener { v, i, ip ->
            val insets =
                i.getInsets(Type.navigationBars() or Type.ime())
            v.updatePadding(bottom = ip.bottom + insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        //TODO animate my keyboard.
        WindowInsetsAnimationCompat(Type.ime(), LinearInterpolator(), 300L)
        val cb = object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
            override fun onProgress(
                insets: WindowInsetsCompat,
                runningAnimations: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat {
                TODO("Not yet implemented")
            }
        }
    }

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

    /**
     * Clear focus of edit text view when touch outside of it.
     * call this at [dispatchTouchEvent] if needed.
     */
    private fun clearEditTextFocus(ev: MotionEvent) {
        val v = currentFocus
        if (ev.action == MotionEvent.ACTION_UP && v is EditText) {
            val rect = Rect()
            v.getGlobalVisibleRect(rect)
            if (!rect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                v.clearFocus()
                windowInsetsController.hide(Type.ime())
            }
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
