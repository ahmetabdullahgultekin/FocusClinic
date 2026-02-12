package com.focusclinic.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val label: String,
    val screen: Screen,
    val icon: ImageVector,
) {
    FOCUS("Focus", Screen.Focus, Icons.Filled.Home),
    CLINIC("Clinic", Screen.Clinic, Icons.Filled.Favorite),
    SHOP("Shop", Screen.Shop, Icons.Filled.ShoppingCart),
    STATS("Stats", Screen.Stats, Icons.Filled.Star),
}
