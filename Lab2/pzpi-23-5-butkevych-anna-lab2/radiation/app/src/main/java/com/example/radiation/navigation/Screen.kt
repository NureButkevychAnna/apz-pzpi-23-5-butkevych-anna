package com.example.radiation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    sealed class Auth : Screen() {
        @Serializable
        data object SignIn : Auth()
        @Serializable
        data object Registration : Auth()
        @Serializable
        data object ForgotPassword : Auth()
    }

    @Serializable
    sealed class Main : Screen() {
        @Serializable
        data object Home : Main()
        @Serializable
        data object Devices : Main()
        @Serializable
        data object Alerts : Main()
        @Serializable
        data object Subscriptions : Main()
        @Serializable
        data object Settings : Main()

        @Serializable
        data class DeviceDetails(val deviceId: String) : Main()

        @Serializable
        data class Readings(val deviceId: String) : Main()

        @Serializable
        data class AlertDetails(val alertId: String) : Main()
    }
}
