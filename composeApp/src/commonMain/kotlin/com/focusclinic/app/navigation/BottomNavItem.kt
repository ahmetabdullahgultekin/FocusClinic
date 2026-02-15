package com.focusclinic.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
) {
    FOCUS(Screen.Focus, Icons.Filled.Home),
    GOALS(Screen.Goals, Icons.Filled.CheckCircle),
    PROFILE(Screen.Profile, Icons.Filled.Favorite),
    SHOP(Screen.Shop, Icons.Filled.ShoppingCart),
    STATS(Screen.Stats, Icons.Filled.Star),
}
