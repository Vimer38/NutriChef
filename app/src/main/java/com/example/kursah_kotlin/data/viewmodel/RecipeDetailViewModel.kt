package com.example.kursah_kotlin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursah_kotlin.data.local.AppDatabase
import com.example.kursah_kotlin.data.remote.dto.RecipeDto
import com.example.kursah_kotlin.data.remote.toEntities
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

data class RecipeDetailUiState(
    val recipe: RecipeDto? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun load(recipeId: String) {
        if (recipeId.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            val recipe = loadRecipeById(context, recipeId)
            var isFavorite = false
            if (recipeId.isNotBlank()) {
                val recipeEntity = database.recipeDao().getRecipeById(recipeId)
                isFavorite = recipeEntity?.isFavorite ?: false
            }
            withContext(Dispatchers.Main) {
                _uiState.value = RecipeDetailUiState(recipe = recipe, isFavorite = isFavorite)
            }
        }
    }

    fun toggleFavorite(recipeId: String, onToggled: () -> Unit) {
        val current = _uiState.value
        val recipe = current.recipe ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val recipeDao = database.recipeDao()
            val newFavoriteState = !current.isFavorite

            val existing = recipeDao.getRecipeById(recipeId)
            if (existing == null) {
                val bundle = recipe.toEntities()
                bundle?.let { b ->
                    val entityWithFavorite = b.recipe.copy(isFavorite = newFavoriteState)
                    recipeDao.upsertRecipes(listOf(entityWithFavorite))
                }
            } else if (existing.isFavorite != newFavoriteState) {
                recipeDao.upsertRecipes(
                    listOf(existing.copy(isFavorite = newFavoriteState))
                )
            } else {
                recipeDao.setFavorite(recipeId, newFavoriteState)
            }

            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(isFavorite = newFavoriteState)
                onToggled()
            }
        }
    }

    private suspend fun loadRecipeById(context: Context, recipeId: String): RecipeDto? {
        return try {
            val jsonString = withContext(Dispatchers.IO) {
                context.assets.open("recipes.json").bufferedReader().use { it.readText() }
            }
            val type = object : TypeToken<List<RecipeDto>>() {}.type
            val recipes = Gson().fromJson<List<RecipeDto>>(jsonString, type) ?: emptyList()
            recipes.find { it.id == recipeId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


