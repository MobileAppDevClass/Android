package com.example.teamproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.teamproject.navigation.Screen
import com.example.teamproject.navigation.bottomNavItems
import com.example.teamproject.ui.screens.BodyInfoScreen
import com.example.teamproject.ui.screens.FriendsScreen
import com.example.teamproject.ui.screens.LoginScreen
import com.example.teamproject.ui.screens.SignupScreen
import com.example.teamproject.ui.screens.WaterTrackingScreen
import com.example.teamproject.ui.theme.TeamProjectTheme
import com.example.teamproject.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeamProjectTheme {
                WaterTrackingApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackingApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val authViewModel: AuthViewModel = viewModel()

    // Show bottom bar and top bar only when not on auth screens
    val isAuthScreen = currentDestination?.route == Screen.Login.route ||
            currentDestination?.route == Screen.Signup.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAuthScreen) {
                TopAppBar(
                    title = { Text("DrinkFlow") },
                    actions = {
                        IconButton(
                            onClick = {
                                authViewModel.logout {
                                    // Navigate to login screen
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "로그아웃"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!isAuthScreen) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.route == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // Navigate to main screen after successful login
                        navController.navigate(Screen.WaterTracking.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToSignup = {
                        navController.navigate(Screen.Signup.route)
                    }
                )
            }
            composable(Screen.Signup.route) {
                SignupScreen(
                    onSignupSuccess = {
                        // Navigate to main screen after successful signup
                        navController.navigate(Screen.WaterTracking.route) {
                            popUpTo(Screen.Signup.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.BodyInfo.route) {
                BodyInfoScreen()
            }
            composable(Screen.WaterTracking.route) {
                WaterTrackingScreen()
            }
            composable(Screen.Friends.route) {
                FriendsScreen()
            }
        }
    }
}