package com.example.kursah_kotlin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursah_kotlin.data.local.AppDatabase
import com.example.kursah_kotlin.data.remote.dto.RecipeDto
import com.example.kursah_kotlin.screens.RecipeCard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SavedRecipesUiState(
    val categories: List<Pair<String, List<RecipeCard>>> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class SavedRecipesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedRecipesUiState())
    val uiState: StateFlow<SavedRecipesUiState> = _uiState.asStateFlow()

    init {
        loadSavedRecipes()
    }

    private fun loadSavedRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favoriteEntities = database.recipeDao().getFavorites()

                val jsonString = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<RecipeDto>>() {}.type
                val allRecipes = Gson().fromJson<List<RecipeDto>>(jsonString, type) ?: emptyList()

                val favoriteIds = favoriteEntities.map { it.id }.toSet()
                val favorites = allRecipes.filter { favoriteIds.contains(it.id) }

                val grouped = favorites
                    .groupBy { it.category ?: "Без категории" }
                    .map { (category, recipes) ->
                        category to recipes.map { recipe ->
                            RecipeCard(
                                id = recipe.id ?: "",
                                title = recipe.title ?: "Без названия",
                                description = recipe.description ?: "Нет описания",
                                time = recipe.timeMinutes?.let { "$it мин" },
                                imageUrl = recipe.imageUrl
                            )
                        }
                    }

                withContext(Dispatchers.Main) {
                    _uiState.value = SavedRecipesUiState(
                        categories = grouped,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _uiState.value = SavedRecipesUiState(
                        categories = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }
}


