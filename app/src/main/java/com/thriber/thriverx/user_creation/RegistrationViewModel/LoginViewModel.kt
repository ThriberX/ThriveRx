package com.thriber.thriverx.user_creation.RegistrationViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val userId: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val firebaseDao: FirebaseDao) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val userId = firebaseDao.login(email, password)
                _uiState.value = LoginUiState.Success(userId)
            } catch (e: Exception) {
                // This catches the exceptions thrown by your FirebaseDao (e.g., "Login failed due to invalid id/password")
                _uiState.value = LoginUiState.Error(e.message ?: "Login failed")
            }
        }
    }
}