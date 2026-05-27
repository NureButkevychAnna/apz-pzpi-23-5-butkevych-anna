package com.example.radiation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.radiation.R
import com.example.radiation.navigation.Screen
import com.example.radiation.ui.components.*
import com.example.radiation.ui.theme.*
import com.example.radiation.ui.screen.alerts.Alerts
import com.example.radiation.ui.viewmodel.AlertsViewModel

@Composable
fun AlertsScreen(
    navController: NavHostController,
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is Alerts.Event.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                Alerts.Event.NavigateBack -> navController.navigateUp()
            }
        }
    }

    RadScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RadTopBar(
                title = stringResource(R.string.alerts_title),
                actions = {
                    TextButton(onClick = { viewModel.onAction(Alerts.Action.Refresh) }) {
                        Text(stringResource(R.string.common_refresh), color = PrimaryBlueLight)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (state.isLoading && state.alerts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlueLight)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 20.dp)
                ) {
                    if (state.alerts.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text(text = stringResource(R.string.alerts_empty), color = OnSurfaceMuted)
                            }
                        }
                    }
                    items(state.alerts) { alert ->
                        RadSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val alertId = alert.id.safeText()
                                    if (alertId.isNotBlank() && alertId != "—") {
                                        navController.navigate(Screen.Main.AlertDetails(alertId))
                                    }
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AlertLevelBadge(level = alert.level)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.message.safeText(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (!alert.acknowledged) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = alert.created_at.safeText(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceDisabled
                                    )
                                }
                                if (!alert.acknowledged) {
                                    IconButton(onClick = { viewModel.onAction(Alerts.Action.Acknowledge(alert.id)) }) {
                                        Text(text = stringResource(R.string.alerts_acknowledge), color = PrimaryBlueLight, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String?.safeText(fallback: String = "—"): String =
    this?.takeIf { it.isNotBlank() } ?: fallback

