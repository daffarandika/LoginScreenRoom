package com.itsrobocon.loginscreenroom.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsrobocon.loginscreenroom.data.repository.AuthResult
import com.itsrobocon.loginscreenroom.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loginUsernameInput: String = "",
    val loginPasswordInput: String = "",
    val registerUsernameInput: String = "",
    val registerPasswordInput: String = "",
    val isLoading: Boolean = false,
    val loggedInUser: String? = null
)

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object NavigateToHome : UiEvent()
    object NavigateToLogin : UiEvent()
}

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun onLoginUsernameChange(username: String) {
        _uiState.update { it.copy(loginUsernameInput = username) }
    }

    fun onLoginPasswordChange(password: String) {
        _uiState.update { it.copy(loginPasswordInput = password) }
    }

    fun onRegisterUsernameChange(username: String) {
        _uiState.update { it.copy(registerUsernameInput = username) }
    }

    fun onRegisterPasswordChange(password: String) {
        _uiState.update { it.copy(registerPasswordInput = password) }
    }

    fun login() {
        val username = uiState.value.loginUsernameInput
        val password = uiState.value.loginPasswordInput

        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Username and password cannot be empty"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.loginUser(username, password)
            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            loggedInUser = result.username,
                            loginUsernameInput = "",
                            loginPasswordInput = ""
                        )
                    }
                    _uiEvent.emit(UiEvent.ShowSnackbar(result.message))
                    _uiEvent.emit(UiEvent.NavigateToHome)
                }
                is AuthResult.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar(result.message))
                }
            }
        }
    }

    fun register() {
        val username = uiState.value.registerUsernameInput
        val password = uiState.value.registerPasswordInput

        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Username and password cannot be empty"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.registerUser(username, password)
            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            registerUsernameInput = "",
                            registerPasswordInput = ""
                        )
                    }
                    _uiEvent.emit(UiEvent.ShowSnackbar(result.message))
                    _uiEvent.emit(UiEvent.NavigateToLogin)
                }
                is AuthResult.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar(result.message))
                }
            }
        }
    }

    fun logout() {
        _uiState.update { it.copy(loggedInUser = null) }
    }
}
