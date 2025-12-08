package com.example.teamproject.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    object Login : Screen(
        route = "login"
    )

    object Signup : Screen(
        route = "signup"
    )

    object BodyInfo : Screen(
        route = "body_info",
        title = "Body Info",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle
    )

    object WaterTracking : Screen(
        route = "water_tracking",
        title = "Water",
        selectedIcon = Icons.Filled.LocalDrink,
        unselectedIcon = Icons.Outlined.LocalDrink
    )

    object Friends : Screen(
        route = "friends",
        title = "Leaderboard",
        selectedIcon = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group
    )
}

val bottomNavItems = listOf(
    Screen.BodyInfo,
    Screen.WaterTracking,
    Screen.Friends
)
