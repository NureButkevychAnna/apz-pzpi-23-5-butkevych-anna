package com.example.radiation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.radiation.R
import com.example.radiation.ui.components.*
import com.example.radiation.ui.devices.DeviceDetails
import com.example.radiation.ui.theme.*
import com.example.radiation.ui.viewmodel.DeviceDetailsViewModel

@Composable
fun DeviceDetailsScreen(
    navController: NavHostController,
    deviceId: String,
    viewModel: DeviceDetailsViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deviceId) {
        viewModel.onAction(DeviceDetails.Action.OnLoad(deviceId))
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                DeviceDetails.Event.OnBack -> navController.navigateUp()
                DeviceDetails.Event.DeviceDeleted -> navController.navigateUp()
                is DeviceDetails.Event.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RadTopBar(
                title = stringResource(R.string.device_details_title),
                onBack = { viewModel.onAction(DeviceDetails.Action.OnBack) },
                actions = {
                    IconButton(onClick = { viewModel.onAction(DeviceDetails.Action.OnRefresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.device_details_refresh), tint = PrimaryBlueLight)
                    }
                    IconButton(onClick = { viewModel.onAction(DeviceDetails.Action.OnDeleteClick) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.device_details_delete), tint = DangerRed)
                    }
                }
            )
        },
        containerColor = Background
    ) { innerPadding ->
        if (state.isLoading && state.device == null) {
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
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                state.device?.let { device ->
                    item { DeviceHeader(device) }
                    
                    item {
                        DeviceStatusCard(
                            isActive = device.is_active,
                            onStatusChange = { viewModel.onAction(DeviceDetails.Action.OnUpdateActiveStatus(it)) }
                        )
                    }

                    item {
                        DeviceInfoCard(
                            tokenId = device.device_token,
                            locationId = device.location_id
                        )
                    }
                }

                if (state.readings.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.device_details_latest_readings),
                            color = OnSurfaceMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(state.readings) { reading ->
                        ReadingItem(reading)
                    }
                } else if (!state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.device_details_no_readings), color = OnSurfaceDisabled)
                        }
                    }
                }

                state.error?.let {
                    item {
                        Text(text = it, color = DangerRed, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceHeader(device: com.example.radiation.data.models.Device) {
    RadSurface(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("📡", fontSize = 32.sp)
            }
            Column {
                Text(text = device.name.safeText(), color = OnBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.common_id_prefix, device.id.safeText()), color = OnSurfaceMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DeviceStatusCard(isActive: Boolean, onStatusChange: (Boolean) -> Unit) {
    RadSurface(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = stringResource(R.string.device_details_work_status), color = OnBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (isActive) stringResource(R.string.device_details_collecting) else stringResource(R.string.device_details_stopped),
                    color = if (isActive) SafeGreen else OnSurfaceDisabled,
                    fontSize = 13.sp
                )
            }
            Switch(
                checked = isActive,
                onCheckedChange = onStatusChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SafeGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = OnSurfaceDisabled.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun DeviceInfoCard(tokenId: String?, locationId: String?) {
    RadSurface(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = stringResource(R.string.device_details_technical_info), color = OnBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            
            InfoRow(label = stringResource(R.string.device_details_token), value = tokenId.safeText())
            InfoRow(label = stringResource(R.string.device_details_location), value = locationId ?: stringResource(R.string.device_details_not_set))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, color = OnSurfaceDisabled, fontSize = 11.sp)
        Text(text = value, color = OnBackground, fontSize = 14.sp)
    }
}

@Composable
fun ReadingItem(reading: com.example.radiation.data.models.SensorReading) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = reading.measured_at.safeText(), color = OnSurfaceDisabled, fontSize = 11.sp)
            Text(text = stringResource(R.string.device_details_radiation_level), color = OnBackground, fontSize = 13.sp)
        }
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = reading.value.toString(), color = PrimaryBlueLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = reading.unit.safeText(), color = OnSurfaceDisabled, fontSize = 11.sp, modifier = Modifier.padding(bottom = 2.dp))
        }
    }
}

private fun String?.safeText(fallback: String = "—"): String =
    this?.takeIf { it.isNotBlank() } ?: fallback

