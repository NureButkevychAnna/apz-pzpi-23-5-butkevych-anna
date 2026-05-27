package com.example.radiation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.radiation.R

data class TopLevelRoute<T : Any>(
    val route: T,
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
)

val topLevelRoutes = listOf(
    TopLevelRoute(
        route = Screen.Main.Home,
        title = R.string.nav_home,
        selectedIcon = android.R.drawable.ic_menu_view,
        unselectedIcon = android.R.drawable.ic_menu_view,
    ),
    TopLevelRoute(
        route = Screen.Main.Devices,
        title = R.string.nav_devices,
        selectedIcon = android.R.drawable.ic_menu_agenda,
        unselectedIcon = android.R.drawable.ic_menu_agenda,
    ),
    TopLevelRoute(
        route = Screen.Main.Alerts,
        title = R.string.nav_alerts,
        selectedIcon = android.R.drawable.ic_menu_info_details,
        unselectedIcon = android.R.drawable.ic_menu_info_details,
    ),
    TopLevelRoute(
        route = Screen.Main.Subscriptions,
        title = R.string.nav_subscriptions,
        selectedIcon = android.R.drawable.ic_menu_send,
        unselectedIcon = android.R.drawable.ic_menu_send,
    ),
    TopLevelRoute(
        route = Screen.Main.Settings,
        title = R.string.nav_settings,
        selectedIcon = android.R.drawable.ic_menu_manage,
        unselectedIcon = android.R.drawable.ic_menu_manage,
    ),
)


