package com.genlz.jetpacks.ui

import android.widget.Toast
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genlz.jetpacks.R
import com.genlz.jetpacks.ui.components.*
import com.genlz.jetpacks.ui.theme.JetpacksTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private const val TAG = "ComposeApp"

@Composable
fun ComposeApp(
    viewModel: MainActivityViewModel,
) {
    val ctx = LocalContext.current
    val destinations = listOf(
        ctx.getString(R.string.community) to R.drawable.ic_baseline_fiber_new_24,
        ctx.getString(R.string.products) to R.drawable.ic_baseline_shopping_bag_24,
        ctx.getString(R.string.service) to R.drawable.ic_baseline_emoji_objects_24,
        ctx.getString(R.string.vip) to R.drawable.ic_baseline_account_circle_24
    )
    val systemUiController = rememberSystemUiController()
    val screenIndex by viewModel.currentScreenIndex.collectAsState()
    JetpacksTheme {
        Scaffold(
//            modifier = Modifier.systemBarsPadding(),
            topBar = {
                SideEffect {
//                    systemUiController.setStatusBarColor(Color(0xffa557f5))
                }
                TopAppBar(
                    title = {
                        Text(text = destinations[screenIndex].first)
                    },
                )
            },
            bottomBar = {
                SideEffect {
//                    systemUiController.setNavigationBarColor(Color(0xffa557f5))
                }
                BottomNavigation {
                    destinations.forEachIndexed { index, dest ->
                        val (name, iconRes) = dest
                        val selected = screenIndex == index
                        val fontSize by animateIntAsState(if (selected) 16 else 8)
                        val iconSize by animateIntAsState(if (selected) 24 else 16)
                        BottomNavigationItem(
                            selected = selected,
                            onClick = { viewModel.onNewScreenSelected(index) },
                            icon = {
                                Icon(
                                    painter = painterResource(iconRes),
                                    contentDescription = name,
                                    modifier = Modifier.size(iconSize.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = name,
                                    maxLines = 1,
                                    fontSize = fontSize.sp
                                )
                            }
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    Toast.makeText(ctx.applicationContext, "Hello", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            when (screenIndex) {
                0 -> CommunityScreen(padding)
                1 -> ProductsScreen()
                2 -> ServiceScreen()
                3 -> VipScreen()
            }
        }
    }
}
