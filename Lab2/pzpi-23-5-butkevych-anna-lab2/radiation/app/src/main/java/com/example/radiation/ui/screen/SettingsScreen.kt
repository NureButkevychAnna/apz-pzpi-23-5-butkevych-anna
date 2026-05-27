package com.example.radiation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.appcompat.app.AppCompatDelegate
import com.example.radiation.R
import com.example.radiation.config.AppLocale
import com.example.radiation.navigation.Screen
import com.example.radiation.ui.components.*
import com.example.radiation.ui.theme.*
import com.example.radiation.ui.screen.settings.Settings
import com.example.radiation.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavHostController) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags().takeIf { it.isNotBlank() } ?: AppLocale.DEFAULT

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                Settings.Event.LoggedOut -> navController.navigate(Screen.Auth.SignIn) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    RadiationTheme {
        RadScaffold(
            topBar = { RadTopBar(title = stringResource(R.string.settings_profile_title)) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlueLight)
                    }
                } else {
                    // Аватар + ім'я
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Surface)
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariant)
                                .border(2.dp, PrimaryBlue, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = state.user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                color = PrimaryBlueLight,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Column {
                            Text(
                                text = state.user?.name ?: stringResource(R.string.settings_user_default),
                                color = OnBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = state.user?.email ?: "",
                                color = OnSurfaceMuted,
                                fontSize = 13.sp,
                            )
                            Text(
                                text = state.user?.role?.uppercase() ?: "",
                                color = PrimaryBlueLight,
                                fontSize = 11.sp,
                            )
                        }
                    }

                    RadSurface(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = stringResource(R.string.settings_language_title),
                                color = OnBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = stringResource(R.string.settings_language_description),
                                color = OnSurfaceMuted,
                                fontSize = 13.sp,
                            )

                            LanguageChoice(
                                title = stringResource(R.string.language_ukrainian),
                                selected = currentLanguage == AppLocale.UKRAINIAN,
                                onClick = { viewModel.onAction(Settings.Action.ChangeLanguage(AppLocale.UKRAINIAN)) },
                            )
                            LanguageChoice(
                                title = stringResource(R.string.language_english),
                                selected = currentLanguage == AppLocale.ENGLISH,
                                onClick = { viewModel.onAction(Settings.Action.ChangeLanguage(AppLocale.ENGLISH)) },
                            )
                        }
                    }

                    // Підписки
                    RadOutlinedButton(
                        text = stringResource(R.string.settings_manage_subscriptions),
                        onClick = { navController.navigate(Screen.Main.Subscriptions) },
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Вихід
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = { viewModel.onAction(Settings.Action.Logout) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerBg,
                            contentColor = DangerRed,
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                    ) {
                        Text(text = stringResource(R.string.settings_logout), fontWeight = FontWeight.Bold)
                    }

                    state.error?.let {
                        Text(text = it, color = DangerRed, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageChoice(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = title, color = OnBackground, fontSize = 14.sp)
    }
}

