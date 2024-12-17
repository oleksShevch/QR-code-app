package com.svvar.coursework.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Scan : BottomNavItem("Сканувати", Icons.Filled.CameraAlt, "scan")
    object Generate : BottomNavItem("Генерувати", Icons.Filled.QrCode2, "generate")
    object History : BottomNavItem("Історія", Icons.Filled.History, "history")
}
