package com.bodakesatish.firebaseauthentication

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController // Create this screen
import org.koin.compose.viewmodel.koinViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation() {
    val authViewModel = koinViewModel<AuthViewModel>()

    val navController = rememberNavController()
    // authState will be null if not logged in, or FirebaseUser object if logged in
    val currentUser by authViewModel.authState.collectAsState()

    // Determine initial start destination
    // This could also be a "Splash" screen that then decides where to go
    val startDestination = if (currentUser != null) Screen.Profile.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true // Good practice
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen( // Implement RegisterScreen similarly to LoginScreen
                onRegisterSuccess = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Login.route) { inclusive = true } // Or popUpTo Register
                        // Or more simply if you always want to clear up to login:
                        // popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                    // Or if Login is not guaranteed to be below Register always:
                    // navController.navigate(Screen.Login.route) {
                    //    popUpTo(Screen.Register.route) { inclusive = true }
                    // }
                }
            )
        }
        composable(Screen.Profile.route) {
            // The LaunchedEffect below handles redirection if currentUser becomes null.
            // So, if we reach here, we assume currentUser is (or was very recently) not null.
            // If currentUser is null *during* composition of ProfileScreen content,
            // ProfileScreen itself should handle showing a loading or appropriate message,
            // rather than AppNavigation trying to navigate from within this composable block.
            ProfileScreen() // Pass the same ViewModel instance
            // `onLogout` callback is removed from ProfileScreen's parameters
            // as authViewModel.logout() will trigger the LaunchedEffect below

        }
    }
    // This is the single source of truth for handling navigation when auth state changes globally.
    LaunchedEffect(currentUser, navController.currentBackStackEntry) { // Observe backstack too to avoid issues during config changes
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentUser == null) {
            // If user is logged out and not already on Login or Register screen
            if (currentRoute != Screen.Login.route && currentRoute != Screen.Register.route) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true } // Clear the entire back stack
                    launchSingleTop = true
                }
            }
        } else {
            // If user is logged in and currently on the Login screen (e.g., after successful login
            // but before the explicit navigation in onLoginSuccess, or app start)
            if (currentRoute == Screen.Login.route) {
                navController.navigate(Screen.Profile.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}
