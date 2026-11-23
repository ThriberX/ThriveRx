package com.thriber.thriverx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thriber.thriverx.user_creation.RegistrationViewModel.LoginUiState
import com.thriber.thriverx.user_creation.RegistrationViewModel.SignupUiState
import com.thriber.thriverx.user_creation.RegistrationViewModel.SignupViewModel

@Composable
fun SignupScreen(
    viewModel: SignupViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.Success) {
            onSuccess()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is SignupUiState.Loading
        ) {
            if (uiState is SignupUiState.Loading) {
                CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
            } else {
                Text("Sign Up")
            }
        }

        if (uiState is SignupUiState.Success) {
            Text("Success! Redirecting...", modifier = Modifier.padding(top = 16.dp))
            // Note: In a real app, handle navigation to Login here via a side-effect
        }
    }
}