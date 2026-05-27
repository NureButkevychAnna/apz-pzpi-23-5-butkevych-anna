package com.example.radiation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.radiation.R
import com.example.radiation.navigation.Screen
import com.example.radiation.ui.auth.register.Register
import com.example.radiation.ui.theme.RadiationTheme
import com.example.radiation.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavHostController) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                Register.Event.OnBack -> {
                    navController.navigate(Screen.Auth.SignIn)
                }
                is Register.Event.OnNavigate -> navController.navigate(Screen.Main.Home)
            }
        }
    }

    RadiationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = stringResource(R.string.register_title))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.name,
                    onValueChange = { viewModel.onAction(Register.Action.OnNameChange(it)) },
                    label = { Text(text = stringResource(R.string.register_name)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = { viewModel.onAction(Register.Action.OnEmailChange(it)) },
                    label = { Text(text = stringResource(R.string.register_email)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = { viewModel.onAction(Register.Action.OnPasswordChange(it)) },
                    label = { Text(text = stringResource(R.string.register_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                )
                state.registerError?.let { Text(text = it) }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.inProgress,
                    onClick = { viewModel.onAction(Register.Action.OnRegister) },
                ) {
                    Text(text = if (state.inProgress) stringResource(R.string.register_loading) else stringResource(R.string.register_create_account))
                }
                Text(
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Auth.SignIn)
                    },
                    text = stringResource(R.string.register_back_to_login),
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    RadiationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = stringResource(R.string.forgot_password_title))
                Text(text = stringResource(R.string.forgot_password_message))
                Button(onClick = {
                    navController.navigate(Screen.Auth.SignIn)
                }) {
                    Text(text = stringResource(R.string.forgot_password_back))
                }
            }
        }
    }
}

// REMOVED DevicesScreen to avoid conflict with new custom screen
// REMOVED DeviceDetailsScreen
// REMOVED ReadingsScreen
// REMOVED AlertsScreen
// REMOVED AlertDetailsScreen
// REMOVED SubscriptionsScreen
