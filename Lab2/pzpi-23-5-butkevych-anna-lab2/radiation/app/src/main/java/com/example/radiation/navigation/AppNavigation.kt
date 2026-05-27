package com.example.radiation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.radiation.ui.screen.*

/**
 * Navigation граф додатку
 * Визначає всі маршрути та переходи між екранами
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    contentPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.SignIn,
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        composable<Screen.Auth.SignIn> {
            LoginScreen(viewModel = hiltViewModel(), navController = navController)
        }
        composable<Screen.Auth.Registration> { RegisterScreen(navController) }
        composable<Screen.Auth.ForgotPassword> { ForgotPasswordScreen(navController) }

        composable<Screen.Main.Home> { HomeScreen(navController) }
        composable<Screen.Main.Devices> { 
            DevicesScreen(navController = navController, viewModel = hiltViewModel()) 
        }
        composable<Screen.Main.DeviceDetails> { backStackEntry ->
            val route: Screen.Main.DeviceDetails = backStackEntry.toRoute()
            DeviceDetailsScreen(
                navController = navController, 
                deviceId = route.deviceId, 
                viewModel = hiltViewModel()
            )
        }
        composable<Screen.Main.Readings> { backStackEntry ->
            val route: Screen.Main.Readings = backStackEntry.toRoute()
            ReadingsScreen(navController = navController, deviceId = route.deviceId)
        }

        composable<Screen.Main.Alerts> { AlertsScreen(navController) }
        composable<Screen.Main.AlertDetails> { backStackEntry ->
            val route: Screen.Main.AlertDetails = backStackEntry.toRoute()
            AlertDetailsScreen(navController = navController, alertId = route.alertId)
        }
        composable<Screen.Main.Subscriptions> { SubscriptionsScreen(navController) }
        composable<Screen.Main.Settings> { SettingsScreen(navController) }
    }
}

