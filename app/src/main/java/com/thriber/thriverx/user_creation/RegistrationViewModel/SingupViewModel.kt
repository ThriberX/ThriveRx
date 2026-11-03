package com.thriber.thriverx.user_creation.RegistrationViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignupUiState {
    data object Idle : SignupUiState()
    data object Loading : SignupUiState()
    data class Success(val userId: String) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}

class SignupViewModel(private val firebaseDao: FirebaseDao) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignupUiState.Loading
            try {
                val userId = firebaseDao.register(email, password)
                _uiState.value = SignupUiState.Success(userId)
            } catch (e: Exception) {
                // Catches "This email is already registered" or other errors from your FirebaseDao
                _uiState.value = SignupUiState.Error(e.message ?: "Registration failed")
            }
        }
    }
}