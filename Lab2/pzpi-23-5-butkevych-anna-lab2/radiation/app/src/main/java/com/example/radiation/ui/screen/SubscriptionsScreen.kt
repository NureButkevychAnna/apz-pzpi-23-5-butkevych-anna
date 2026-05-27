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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.radiation.R
import com.example.radiation.ui.components.*
import com.example.radiation.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.font.FontWeight
import com.example.radiation.ui.screen.subscriptions.Subscriptions
import com.example.radiation.ui.viewmodel.SubscriptionsViewModel

@Composable
fun SubscriptionsScreen(
    navController: NavHostController,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is Subscriptions.Event.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    RadScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RadTopBar(
                title = stringResource(R.string.subscriptions_title),
                onBack = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { viewModel.onAction(Subscriptions.Action.ToggleCreateDialog) }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.subscriptions_create), tint = PrimaryBlueLight)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (state.isLoading && state.subscriptions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlueLight)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 20.dp)
                ) {
                    if (state.subscriptions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text(text = stringResource(R.string.subscriptions_empty), color = OnSurfaceMuted)
                            }
                        }
                    }
                    items(state.subscriptions) { sub ->
                        RadSurface(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sub.channel,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PrimaryBlueLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.common_id_prefix, sub.id),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceDisabled
                                    )
                                    if (sub.criteria != null) {
                                        Text(
                                            text = stringResource(R.string.subscriptions_criteria_prefix, sub.criteria),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceMuted
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.onAction(Subscriptions.Action.Delete(sub.id)) }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.subscriptions_delete), tint = DangerRed)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.isCreating) {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(Subscriptions.Action.ToggleCreateDialog) },
                containerColor = Surface,
                title = { Text(stringResource(R.string.subscriptions_new_title), color = OnBackground) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RadTextField(
                            value = state.newChannel,
                            onValueChange = { viewModel.onAction(Subscriptions.Action.OnChannelChange(it)) },
                            label = stringResource(R.string.subscriptions_channel_label)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.onAction(Subscriptions.Action.Create) }) {
                        Text(stringResource(R.string.subscriptions_create), color = PrimaryBlueLight)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onAction(Subscriptions.Action.ToggleCreateDialog) }) {
                        Text(stringResource(R.string.subscriptions_cancel), color = OnSurfaceMuted)
                    }
                }
            )
        }
    }
}


