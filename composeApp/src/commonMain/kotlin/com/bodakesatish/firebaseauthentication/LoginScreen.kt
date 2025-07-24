package com.bodakesatish.firebaseauthentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// com/example/yourapp/presentation/auth/LoginScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by authViewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is AuthResult.Success -> {
                onLoginSuccess()
                authViewModel.clearStates() // Clear state after navigation
            }
            is AuthResult.Error -> {
                scope.launch { snackbarHostState.showSnackbar("Login Failed: ${state.message}") }
                authViewModel.clearStates()
            }
            is AuthResult.Loading -> { /* Show loading indicator */ }
            null -> { /* Initial state */ }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                // visualTransformation = PasswordVisualTransformation() // For password hiding
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { authViewModel.login(email, password) }, enabled = loginState !is AuthResult.Loading) {
                if (loginState is AuthResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Login")
                }
            }
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }
        }
    }
}
