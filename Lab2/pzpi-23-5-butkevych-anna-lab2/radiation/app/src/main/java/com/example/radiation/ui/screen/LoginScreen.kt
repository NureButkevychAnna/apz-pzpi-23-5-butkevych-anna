package com.example.radiation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.radiation.R
import com.example.radiation.navigation.Screen
import com.example.radiation.ui.auth.login.Login
import com.example.radiation.ui.components.*
import com.example.radiation.ui.theme.*
import com.example.radiation.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                Login.Event.OnBack -> navController.navigateUp()
                is Login.Event.OnNavigate -> navController.navigate(event.route)
            }
        }
    }

    RadiationTheme {
        RadScaffold { innerPadding ->
            LoginScreenContent(
                state = state,
                onAction = viewModel::onAction,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun LoginScreenContent(
    state: Login.State,
    onAction: (Login.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Лого
        Text(text = "☢", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.app_name),
            color = PrimaryBlueLight,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.login_subtitle),
            color = OnSurfaceDisabled,
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.height(40.dp))

        RadSurface(modifier = Modifier.fillMaxWidth()) {
            RadTextField(
                value = state.email,
                onValueChange = { onAction(Login.Action.OnEmailChange(it)) },
                label = stringResource(R.string.login_email),
            )
            Spacer(modifier = Modifier.height(12.dp))
            RadTextField(
                value = state.password,
                onValueChange = { onAction(Login.Action.OnPasswordChange(it)) },
                label = stringResource(R.string.login_password),
                visualTransformation = PasswordVisualTransformation(),
            )

            state.loginError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = DangerRed, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
            RadButton(
                text = if (state.inProgress) stringResource(R.string.login_loading) else stringResource(R.string.login_button),
                onClick = { onAction(Login.Action.OnLogIn) },
                enabled = !state.inProgress,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.clickable {
                onAction(Login.Action.OnNavigate(Screen.Auth.ForgotPassword))
            },
            text = stringResource(R.string.login_forgot_password),
            color = OnSurfaceMuted,
            fontSize = 13.sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.clickable {
                onAction(Login.Action.OnNavigate(Screen.Auth.Registration))
            }
        ) {
            Text(text = stringResource(R.string.login_no_account) + " ", color = OnSurfaceMuted, fontSize = 13.sp)
            Text(text = stringResource(R.string.login_registration), color = PrimaryBlueLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}