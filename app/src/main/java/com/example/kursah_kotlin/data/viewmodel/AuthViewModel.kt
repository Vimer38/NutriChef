package com.example.kursah_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursah_kotlin.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val authError: String? = null,
    val isRegisterMode: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepositoryImpl,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkAlreadyAuthenticated(onAlreadyAuthenticated: () -> Unit) {
        if (firebaseAuth.currentUser != null) {
            onAlreadyAuthenticated()
        }
    }

    fun setRegisterMode(isRegister: Boolean) {
        _uiState.value = _uiState.value.copy(
            isRegisterMode = isRegister,
            authError = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(authError = null)
    }

    fun authenticate(
        email: String,
        password: String,
        isRegister: Boolean,
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.isLoading) return

        _uiState.value = _uiState.value.copy(isLoading = true, authError = null)

        viewModelScope.launch(Dispatchers.IO) {
            val result = if (isRegister) {
                userRepository.signUp(email, password)
            } else {
                userRepository.signIn(email, password)
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, authError = null)
                onSuccess()
            }.onFailure { exception ->
                val errorMsg = exception.message?.lowercase() ?: ""
                val message = when {
                    errorMsg.contains("invalid-email") -> "Некорректный email адрес"
                    errorMsg.contains("wrong-password") -> "Неверный пароль"
                    errorMsg.contains("user-not-found") -> "Пользователь не найден"
                    errorMsg.contains("email-already-in-use") -> "Пользователь с таким email уже существует"
                    errorMsg.contains("weak-password") -> "Пароль слишком слабый. Минимум 6 символов"
                    errorMsg.contains("network") -> "Ошибка сети. Проверьте подключение"
                    errorMsg.contains("malformed") || errorMsg.contains("incorrect") || errorMsg.contains("walformed") ->
                        "Неверный email или пароль"
                    else -> "Ошибка авторизации. Попробуйте снова"
                }
                _uiState.value = _uiState.value.copy(isLoading = false, authError = message)
            }
        }
    }
}


