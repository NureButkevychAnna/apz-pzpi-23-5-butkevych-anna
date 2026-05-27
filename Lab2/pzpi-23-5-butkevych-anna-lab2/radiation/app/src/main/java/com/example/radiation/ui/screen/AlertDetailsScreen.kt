package com.example.radiation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.radiation.R
import com.example.radiation.ui.components.*
import com.example.radiation.ui.theme.*
import com.example.radiation.ui.viewmodel.AlertDetailsViewModel
import com.example.radiation.ui.screen.alerts.AlertDetails

@Composable
fun AlertDetailsScreen(
    navController: NavHostController,
    alertId: String,
    viewModel: AlertDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(alertId) {
        viewModel.onAction(AlertDetails.Action.Load(alertId))
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AlertDetails.Event.NavigateBack -> navController.navigateUp()
                is AlertDetails.Event.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    RadScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RadTopBar(
                title = stringResource(R.string.alert_details_title),
                onBack = { viewModel.onAction(AlertDetails.Action.Back) }
            )
        }
    ) { innerPadding ->
        if (state.isLoading && state.alert == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlueLight)
            }
        } else {
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.alert?.let { alert ->
                    RadSurface {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AlertLevelBadge(level = alert.level.safeText(fallback = "warning"))
                            
                            Text(
                                text = alert.message.safeText(),
                                style = MaterialTheme.typography.titleLarge,
                                color = OnBackground
                            )
                            
                            HorizontalDivider(color = BorderColor)
                            
                            DetailItem(label = stringResource(R.string.alert_details_id), value = alert.id.safeText())
                            DetailItem(label = stringResource(R.string.alert_details_device), value = alert.device_id.safeText())
                            DetailItem(label = stringResource(R.string.alert_details_created), value = alert.created_at.safeText())
                            DetailItem(label = stringResource(R.string.alert_details_status), value = if (alert.acknowledged) stringResource(R.string.alert_details_handled) else stringResource(R.string.alert_details_new))

                            if (!alert.acknowledged) {
                                Spacer(modifier = Modifier.height(8.dp))
                                RadButton(
                                    text = stringResource(R.string.alert_details_confirm),
                                    onClick = { viewModel.onAction(AlertDetails.Action.Acknowledge) }
                                )
                            }
                        }
                    }
                }

                state.error?.let {
                    Text(text = it, color = DangerRed)
                }
            }
        }
    }
}

private fun String?.safeText(fallback: String = "—"): String =
    this?.takeIf { it.isNotBlank() } ?: fallback

@Composable
private fun DetailItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = OnSurfaceMuted, style = MaterialTheme.typography.bodySmall)
        Text(text = value, color = OnBackground, style = MaterialTheme.typography.bodySmall)
    }
}
