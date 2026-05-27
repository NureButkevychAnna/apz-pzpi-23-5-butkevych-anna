package com.example.radiation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.radiation.navigation.Screen
import com.example.radiation.ui.components.*
import com.example.radiation.ui.devices.Devices
import com.example.radiation.ui.theme.*
import com.example.radiation.ui.viewmodel.DevicesViewModel

@Composable
fun DevicesScreen(
    navController: NavHostController,
    viewModel: DevicesViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                Devices.Event.OnBack -> navController.navigateUp()
                is Devices.Event.OnNavigateToDetails -> {
                    navController.navigate(Screen.Main.DeviceDetails(event.deviceId))
                }
                is Devices.Event.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RadTopBar(
                title = stringResource(R.string.devices_title),
                actions = {
                    IconButton(onClick = { viewModel.onAction(Devices.Action.OnRefresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.common_refresh), tint = PrimaryBlueLight)
                    }
                }
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Форма додавання нового пристрою
            AddDeviceCard(
                name = state.newDeviceName,
                isAdding = state.isAddingDevice,
                onNameChange = { viewModel.onAction(Devices.Action.OnNewDeviceNameChange(it)) },
                onAddClick = { viewModel.onAction(Devices.Action.OnAddDeviceClick) }
            )

            if (state.isLoading && state.devices.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlueLight)
                }
            } else {
                Text(
                        text = stringResource(R.string.devices_list_title, state.devices.size),
                    color = OnSurfaceMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(state.devices) { device ->
                        DeviceItem(
                            device = device,
                            onClick = { viewModel.onAction(Devices.Action.OnDeviceClick(device.id)) }
                        )
                    }
                }
            }

            if (state.error != null && state.devices.isEmpty()) {
                Text(
                    text = state.error!!,
                    color = DangerRed,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddDeviceCard(
    name: String,
    isAdding: Boolean,
    onNameChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    RadSurface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.devices_add_title),
                color = OnBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.devices_name_placeholder)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlueLight,
                    unfocusedBorderColor = BorderColor
                )
            )

            Button(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && !isAdding,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (isAdding) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.devices_register_button))
                }
            }
        }
    }
}

@Composable
fun DeviceItem(
    device: com.example.radiation.data.models.Device,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("📡", fontSize = 24.sp)
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                color = OnBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.common_id_prefix, device.id.take(8)),
                color = OnSurfaceMuted,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (device.is_active) SafeGreen.copy(alpha = 0.1f) else OnSurfaceDisabled.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (device.is_active) stringResource(R.string.devices_active) else stringResource(R.string.devices_offline),
                color = if (device.is_active) SafeGreen else OnSurfaceDisabled,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


