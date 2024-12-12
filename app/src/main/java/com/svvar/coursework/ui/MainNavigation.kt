package com.svvar.coursework.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.svvar.coursework.ui.screens.ScanQRCodeScreen
import com.svvar.coursework.ui.components.BottomNavItem
import com.svvar.coursework.ui.components.BottomNavigationBar
import com.svvar.coursework.ui.screens.*

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
//            composable(BottomNavItem.Home.route) { HomeScreen(navController) }
            composable(BottomNavItem.Scan.route) { ScanQRCodeScreen(navController) }
            composable(BottomNavItem.Generate.route) { GenerateQRCodeScreen(navController) }
            composable(BottomNavItem.History.route) { HistoryScreen(navController) }
        }
    }
}
