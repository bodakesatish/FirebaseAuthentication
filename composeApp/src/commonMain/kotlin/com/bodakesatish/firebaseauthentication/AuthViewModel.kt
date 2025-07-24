package com.bodakesatish.firebaseauthentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {

    // Observe Firebase Auth state
    val authState: StateFlow<FirebaseUser?> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authRepository.currentUser)

    private val _registrationState = MutableStateFlow<AuthResult?>(null)
    val registrationState: StateFlow<AuthResult?> = _registrationState.asStateFlow()

    private val _loginState = MutableStateFlow<AuthResult?>(null)
    val loginState: StateFlow<AuthResult?> = _loginState.asStateFlow()

    // Observe local user profile
    val localUserProfile: StateFlow<ProfileEntity?> = authState.flatMapLatest { firebaseUser ->
        firebaseUser?.uid?.let { uid ->
            val profile = authRepository.getLocalUserProfile(uid)
            profile
        } ?: flowOf(null) // Emit null if no user is logged in
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    fun register(email: String, pass: String, fullName: String) {
        viewModelScope.launch {
            authRepository.register(email, pass, fullName).collect { result ->
                _registrationState.value = result
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.login(email, pass).collect { result ->
                _loginState.value = result
                if (result == AuthResult.Success) {
                    authState.value?.uid?.let {
                        authRepository.updateUserLastLoginTime(it)
                    }
                } else {
                    // Optionally clear states if login fails
                    _loginState.value = null
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            // Optionally clear states
            _loginState.value = null
            _registrationState.value = null
        }
    }

    fun clearStates() {
        _loginState.value = null
        _registrationState.value = null
    }
}
