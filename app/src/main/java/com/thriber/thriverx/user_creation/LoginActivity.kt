package com.thriber.thriverx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thriber.thriverx.user_creation.RegistrationViewModel.LoginUiState
import com.thriber.thriverx.user_creation.RegistrationViewModel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToReset: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome Back", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is LoginUiState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            enabled = uiState !is LoginUiState.Loading
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is LoginUiState.Loading
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onNavigateToSignup) {
            Text("Sign Up")
        }
        TextButton(onClick = onNavigateToReset) {
            Text("Forgot Password?")
        }

        if (uiState is LoginUiState.Error) {
            Text((uiState as LoginUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}