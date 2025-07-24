package com.bodakesatish.firebaseauthentication.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val localUserProfile by authViewModel.localUserProfile.collectAsState()
    val firebaseUser by authViewModel.authState.collectAsState() // To get verified status if needed


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        //onLogout() // Navigate after logout logic in ViewModel completes
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (localUserProfile == null && firebaseUser != null) {
                // Still loading profile from Room or profile doesn't exist locally yet
                CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                Text(
                    "Loading profile...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else if (localUserProfile != null) {
                val user = localUserProfile!! // Safe due to the check above

                Spacer(modifier = Modifier.height(16.dp))

                // You can add a Profile Picture Composable here later
                // e.g., using Coil or Glide library

                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (firebaseUser?.isEmailVerified == false) {
                    Text(
                        "Email not verified",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    // You could add a "Resend Verification Email" button here
                }

                Divider(modifier = Modifier.padding(vertical = 24.dp))

                ProfileInfoRow(label = "User ID (UID):", value = user.uid)
                ProfileInfoRow(
                    label = "Registered On:",
                    value = user.registrationTimestamp.toString()//SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(user.registrationTimestamp))
                )


// Define your desired format (can be a top-level val or defined locally)
// This is similar to "MMM dd, yyyy, hh:mm a"
// Note: kotlinx-datetime formatting is powerful but different from SimpleDateFormat patterns.
// Building an exact "MMM dd, yyyy, hh:mm a" equivalent requires careful construction.
// For simplicity, let's start with a common ISO-like format and then refine.

                val now = Clock.System.now()
                val currentDateTime = now.toLocalDateTime(TimeZone.of("Asia/Calcutta"))
                val formattedTime = currentDateTime.format(
                    LocalDateTime.Format {
                        dayOfMonth()
                        char('/')
                        monthNumber()
                        char('/')
                        year()
                        char(' ')
                        char(' ')
                        hour()
                        char(':')
                        minute()
                        char(':')
                        second()
                    }
                )
                val displayTimeFormat = LocalDateTime.Format {
                    dayOfMonth()
                    char('/')
                    monthNumber()
                    char('/')
                    year()
                    char(' ')
                    char(' ') // Two spaces, as in your original
                    hour()
                    char(':')
                    minute()
                    char(':')
                    second()
                }
                user.lastLoginTimestamp?.let {
                    val loginInstant = Instant.fromEpochMilliseconds(it)
                    val displayTimeZone = TimeZone.of("Asia/Calcutta") // Or TimeZone.currentSystemDefault()
                    val loginLocalDateTime: LocalDateTime = loginInstant.toLocalDateTime(displayTimeZone)

                    val formattedTime: String = displayTimeFormat.format(loginLocalDateTime)
                    ProfileInfoRow(
                        label = "Last Login:",
                        value = formattedTime
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // Pushes content to top and button to bottom

                // Add other profile information or actions here
                // e.g., "Edit Profile" button, "Change Password"

            } else if (firebaseUser == null) {
                // This case should ideally be handled by navigation redirecting to login if not authenticated
                Text("Not logged in. Please login to view your profile.")
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(0.4f) // Adjust weight as needed
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}
