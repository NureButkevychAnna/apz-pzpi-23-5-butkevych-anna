package com.example.radiation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.navigation.NavHostController
import com.example.radiation.R
import com.example.radiation.navigation.Screen
import com.example.radiation.ui.components.*
import com.example.radiation.ui.theme.*
import com.example.radiation.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    RadiationTheme {
        RadScaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "☢", fontSize = 20.sp)
                        Text(text = stringResource(R.string.app_name), color = PrimaryBlueLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { viewModel.refresh() }) {
                        Text(text = "↻", color = PrimaryBlueLight, fontSize = 18.sp)
                    }
                }
            }
        ) { innerPadding ->
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlueLight)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (state.error != null) {
                        item {
                            RadSurface(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = stringResource(R.string.home_api_error_title),
                                        color = DangerRed,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    SelectionContainer {
                                        Text(
                                            text = state.error.orEmpty(),
                                            color = OnBackground,
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Останнє показання
                    item {
                        val lastReading = state.readings.firstOrNull()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceVariant)
                                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                                .clickable {
                                    lastReading?.device_id?.let { id ->
                                        navController.navigate(Screen.Main.DeviceDetails(id))
                                    }
                                }
                                .padding(20.dp),
                        ) {
                            Text(text = stringResource(R.string.home_current_level), color = OnSurfaceDisabled, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = lastReading?.value?.toString() ?: "—",
                                    color = PrimaryBlueLight,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = lastReading?.unit ?: "µSv/h",
                                    color = OnSurfaceDisabled,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val unreadAlerts = state.alerts.count { !it.acknowledged }
                            AlertLevelBadge(
                                level = when {
                                    unreadAlerts == 0 -> "safe"
                                    state.alerts.any { !it.acknowledged && it.level == "critical" } -> "critical"
                                    state.alerts.any { !it.acknowledged && it.level == "danger" } -> "danger"
                                    else -> "warning"
                                }
                            )
                        }
                    }

                    // Статистика
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = "📡",
                                value = state.devices.size.toString(),
                                label = stringResource(R.string.home_devices_label),
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = "🔔",
                                value = state.alerts.count { !it.acknowledged }.toString(),
                                label = stringResource(R.string.home_alerts_label),
                                valueColor = if (state.alerts.any { !it.acknowledged }) WarningAmber else SafeGreen,
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = "📊",
                                value = state.readings.size.toString(),
                                label = stringResource(R.string.home_readings_label),
                            )
                        }
                    }

                    // Останні тривоги
                    if (state.alerts.isNotEmpty()) {
                        item {
                            Text(text = stringResource(R.string.home_recent_alerts), color = OnSurfaceMuted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                        items(state.alerts.take(3)) { alert ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Surface)
                                    .border(1.dp, if (!alert.acknowledged) alertBorderColor(alert.level) else BorderColor, RoundedCornerShape(12.dp))
                                    .clickable {
                                        navController.navigate(Screen.Main.AlertDetails(alert.id))
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                AlertLevelBadge(level = alert.level)
                                Text(
                                    text = alert.message,
                                    color = if (alert.acknowledged) OnSurfaceMuted else OnBackground,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String,
    valueColor: androidx.compose.ui.graphics.Color = OnBackground,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(text = value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = OnSurfaceDisabled, fontSize = 10.sp)
    }
}