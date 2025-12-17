package com.example.kursah_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursah_kotlin.data.local.entities.UserEntity
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

data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val age: String = "",
    val email: String = "",
    val goal: String = "",
    val photoPath: String? = null,
    val nickname: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepositoryImpl,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun toggleEditing() {
        _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
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

    fun onGoalChange(value: String) {
        _uiState.value = _uiState.value.copy(goal = value)
    }

    fun onNicknameChange(value: String) {
        _uiState.value = _uiState.value.copy(nickname = value)
    }

    fun onPhotoPathChange(value: String?) {
        _uiState.value = _uiState.value.copy(photoPath = value)
        saveProfile(partial = true)
    }

    fun loadProfile() {
        val user = firebaseAuth.currentUser ?: return
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val profile: UserEntity? = userRepository.getUserProfile(user.uid)
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    firstName = profile?.firstName.orEmpty(),
                    lastName = profile?.lastName.orEmpty(),
                    age = profile?.age.orEmpty(),
                    email = profile?.email ?: user.email.orEmpty(),
                    goal = profile?.goal.orEmpty(),
                    photoPath = profile?.photoPath,
                    nickname = profile?.nickname.orEmpty(),
                    isLoading = false
                )
            }
        }
    }

    fun saveProfile(partial: Boolean = false) {
        val user = firebaseAuth.currentUser ?: return
        val state = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUserProfile(
                userId = user.uid,
                firstName = state.firstName.takeIf { it.isNotBlank() },
                lastName = state.lastName.takeIf { it.isNotBlank() },
                age = state.age.takeIf { it.isNotBlank() },
                goal = state.goal.takeIf { it.isNotBlank() },
                photoPath = state.photoPath,
                nickname = state.nickname.takeIf { it.isNotBlank() }
            )
            if (!partial) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(isEditing = false)
                }
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.signOut()
            withContext(Dispatchers.Main) {
                onSignedOut()
            }
        }
    }
}


