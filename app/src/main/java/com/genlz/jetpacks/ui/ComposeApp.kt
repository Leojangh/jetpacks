package com.genlz.jetpacks.ui

import android.widget.Toast
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.genlz.jetpacks.R
import com.genlz.jetpacks.ui.theme.JetpacksTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private const val TAG = "ComposeApp"

@Composable
fun ComposeApp() {
    val ctx = LocalContext.current
    var currentScreen by remember { mutableStateOf(0) }
    val destinations = listOf(
        ctx.getString(R.string.community) to R.drawable.ic_baseline_fiber_new_24,
        ctx.getString(R.string.products) to R.drawable.ic_baseline_shopping_bag_24,
        ctx.getString(R.string.service) to R.drawable.ic_baseline_emoji_objects_24,
        ctx.getString(R.string.vip) to R.drawable.ic_baseline_account_circle_24
    )
    val systemUiController = rememberSystemUiController()
    JetpacksTheme {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            topBar = {
                SideEffect {
                    systemUiController.setStatusBarColor(Color(0xffa557f5))
                }
                TopAppBar(
                    title = {
                        Text(text = destinations[currentScreen].first)
                    },
                )
            },
            bottomBar = {
                SideEffect {
                    systemUiController.setNavigationBarColor(Color(0xffa557f5))
                }
                BottomNavigation {
                    destinations.forEachIndexed { index, dest ->
                        val (name, iconRes) = dest
                        BottomNavigationItem(
                            selected = currentScreen == index,
                            onClick = { currentScreen = index },
                            icon = {
                                Icon(
                                    painter = painterResource(iconRes),
                                    contentDescription = name
                                )
                            },
                            label = { Text(text = name) }
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
        ) {

        }
    }
}
