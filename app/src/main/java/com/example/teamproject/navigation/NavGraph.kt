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
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object BodyInfo : Screen(
        route = "body_info",
        title = "신체정보",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle
    )

    object WaterTracking : Screen(
        route = "water_tracking",
        title = "물 기록",
        selectedIcon = Icons.Filled.LocalDrink,
        unselectedIcon = Icons.Outlined.LocalDrink
    )

    object Friends : Screen(
        route = "friends",
        title = "친구",
        selectedIcon = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group
    )
}

val bottomNavItems = listOf(
    Screen.BodyInfo,
    Screen.WaterTracking,
    Screen.Friends
)
