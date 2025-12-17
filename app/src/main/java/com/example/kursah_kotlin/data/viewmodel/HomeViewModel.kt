package com.example.kursah_kotlin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursah_kotlin.data.remote.dto.RecipeDto
import com.example.kursah_kotlin.data.repository.UserRepositoryImpl
import com.example.kursah_kotlin.screens.RecipeCard
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUiState(
    val firstName: String = "Пользователь",
    val photoPath: String? = null,
    val allRecipes: List<RecipeCard> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepositoryImpl,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser

            val (firstName, photoPath) = if (currentUser != null) {
                val profile = userRepository.getUserProfile(currentUser.uid)
                (profile?.firstName ?: currentUser.displayName ?: "Пользователь") to profile?.photoPath
            } else {
                "Пользователь" to null
            }

            val (cards, _) = loadRecipesFromAssets(context)

            withContext(Dispatchers.Main) {
                _uiState.value = HomeUiState(
                    firstName = firstName,
                    photoPath = photoPath,
                    allRecipes = cards,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadRecipesFromAssets(
        context: Context
    ): Pair<List<RecipeCard>, Exception?> {
        return try {
            val jsonString = withContext(Dispatchers.IO) {
                context.assets.open("recipes.json")
                    .bufferedReader()
                    .use { it.readText() }
            }
            if (jsonString.isEmpty()) {
                return Pair(emptyList(), IOException("recipes.json пустой"))
            }
            val type = object : TypeToken<List<RecipeDto>>() {}.type
            val recipes = Gson().fromJson<List<RecipeDto>>(jsonString, type) ?: emptyList()
            val cards = recipes.mapNotNull { dto ->
                val id = dto.id ?: return@mapNotNull null
                val title = dto.title ?: return@mapNotNull null
                RecipeCard(
                    id = id,
                    title = title,
                    description = dto.description
                        ?: dto.nutrients?.calories?.let { "$it ккал" }
                        ?: "Рецепт",
                    time = dto.timeMinutes?.let { "$it мин" },
                    category = dto.category,
                    diets = dto.tags,
                    difficulty = dto.difficulty,
                    imageUrl = dto.imageUrl,
                    rating = dto.rating,
                    isRecipeOfWeek = dto.isRecipeOfWeek,
                    calories = dto.nutrients?.calories,
                    protein = dto.nutrients?.protein,
                    fat = dto.nutrients?.fat,
                    carbs = dto.nutrients?.carbs
                )
            }
            Pair(cards, null)
        } catch (e: Exception) {
            Pair(emptyList(), e)
        }
    }
}


