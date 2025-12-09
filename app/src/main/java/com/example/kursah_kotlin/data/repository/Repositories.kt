package com.example.kursah_kotlin.data.repository

import com.example.kursah_kotlin.data.model.Ingredient
import com.example.kursah_kotlin.data.model.Recipe
import com.example.kursah_kotlin.data.model.UserProfile


interface RecipesRepository {
    suspend fun upsertRecipes(recipes: List<Recipe>)
    suspend fun getRecipeById(id: String): Recipe?
    suspend fun searchRecipes(
        query: String?,
        maxTimeMinutes: Int? = null,
        requiredIngredients: List<Ingredient> = emptyList(),
        nutrientFilters: NutrientFilter = NutrientFilter()
    ): List<Recipe>

    suspend fun toggleFavorite(recipeId: String, isFavorite: Boolean)
    suspend fun getFavorites(): List<Recipe>
}

data class NutrientFilter(
    val maxCalories: Int? = null,
    val minProtein: Double? = null,
    val maxFat: Double? = null,
    val maxCarbs: Double? = null,
    val micronutrients: Map<String, ClosedFloatingPointRange<Double>> = emptyMap()
)

interface IngredientsRepository {
    suspend fun upsertUserIngredients(ingredients: List<Ingredient>)
    suspend fun getUserIngredients(): List<Ingredient>
}

interface UserRepository {
    suspend fun getProfile(): UserProfile?
    suspend fun saveProfile(profile: UserProfile)
}

interface SyncRepository {
    suspend fun syncRecipes()
    suspend fun syncFavorites()
    suspend fun syncUserData()
}

