package com.example.radiation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.radiation.R
import com.example.radiation.ui.components.*
import com.example.radiation.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import com.example.radiation.ui.screen.readings.Readings
import com.example.radiation.ui.viewmodel.ReadingsViewModel

@Composable
fun ReadingsScreen(
    navController: NavHostController,
    deviceId: String,
    viewModel: ReadingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(deviceId) {
        viewModel.onAction(Readings.Action.Load(deviceId))
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                Readings.Event.NavigateBack -> navController.navigateUp()
            }
        }
    }

    RadScaffold(
        topBar = {
            RadTopBar(
                title = stringResource(R.string.readings_title),
                onBack = { viewModel.onAction(Readings.Action.Back) }
            )
        }
    ) { innerPadding ->
        when (val s = state) {
            is Readings.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlueLight)
                }
            }
            is Readings.State.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(20.dp), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = DangerRed)
                }
            }
            is Readings.State.Content -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 20.dp)
                ) {
                    item {
                        Text(text = stringResource(R.string.readings_device_prefix, s.deviceId), style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    if (s.readings.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                                Text(text = stringResource(R.string.readings_empty), color = OnSurfaceDisabled)
                            }
                        }
                    }
                    items(s.readings) { reading ->
                        RadSurface(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = reading.measured_at.safeText(), fontSize = 10.sp, color = OnSurfaceDisabled)
                                    Text(text = stringResource(R.string.readings_radiation_level), style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(
                                    text = stringResource(R.string.readings_value_with_unit, reading.value, reading.unit.safeText()),
                                    color = PrimaryBlueLight,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
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


