package com.example.kursah_kotlin.viewmodel

import android.net.Uri
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
import kotlinx.coroutines.withContext

data class UserInfoUiState(
    val firstName: String = "",
    val lastName: String = "",
    val age: String = "",
    val nickname: String = "",
    val photoUri: Uri? = null,
    val photoName: String = "Фото не выбрано"
)

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val userRepository: UserRepositoryImpl,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserInfoUiState())
    val uiState: StateFlow<UserInfoUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val currentUser = firebaseAuth.currentUser ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val userProfile = userRepository.getUserProfile(currentUser.uid)
            userProfile?.let {
                val uri = it.photoPath?.let { path -> Uri.parse(path) }
                val photoName = if (uri != null) "Фото выбрано" else "Фото не выбрано"
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        firstName = it.firstName ?: "",
                        lastName = it.lastName ?: "",
                        age = it.age ?: "",
                        nickname = it.nickname ?: "",
                        photoUri = uri,
                        photoName = photoName
                    )
                }
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
    }

    fun onAgeChange(value: String) {
        _uiState.value = _uiState.value.copy(age = value)
    }

    fun onNicknameChange(value: String) {
        _uiState.value = _uiState.value.copy(nickname = value)
    }

    fun onPhotoSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(
            photoUri = uri,
            photoName = uri?.lastPathSegment ?: "Фото выбрано"
        )
    }

    fun save(onComplete: () -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onComplete()
            return
        }
        val state = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUserProfile(
                userId = currentUser.uid,
                firstName = state.firstName.takeIf { it.isNotEmpty() },
                lastName = state.lastName.takeIf { it.isNotEmpty() },
                age = state.age.takeIf { it.isNotEmpty() },
                nickname = state.nickname.takeIf { it.isNotEmpty() },
                photoPath = state.photoUri?.toString()
            )
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}


