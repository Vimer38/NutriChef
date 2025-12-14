package com.example.kursah_kotlin.data.repository

import com.example.kursah_kotlin.data.local.dao.RecipeDao
import com.example.kursah_kotlin.data.local.entities.RecipeEntity
import com.example.kursah_kotlin.data.model.Ingredient
import com.example.kursah_kotlin.data.model.Recipe

class RecipesRepositoryImpl(
    private val recipeDao: RecipeDao
) : RecipesRepository {

    override suspend fun upsertRecipes(recipes: List<Recipe>) {
        // TODO: Implement mapping from Recipe to RecipeEntity
        throw NotImplementedError("Not yet implemented")
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        val entity = recipeDao.getRecipeById(id) ?: return null
        return entity.toRecipe()
    }

    override suspend fun searchRecipes(
        query: String?,
        maxTimeMinutes: Int?,
        requiredIngredients: List<Ingredient>,
        nutrientFilters: NutrientFilter
    ): List<Recipe> {
        // Базовый поиск через DAO
        val entities = recipeDao.searchBasic(query, maxTimeMinutes)
        return entities.map { it.toRecipe() }
    }

    override suspend fun toggleFavorite(recipeId: String, isFavorite: Boolean) {
        recipeDao.setFavorite(recipeId, isFavorite)
    }

    override suspend fun getFavorites(): List<Recipe> {
        val entities = recipeDao.getFavorites()
        return entities.map { it.toRecipe() }
    }

    // Простой маппер для преобразования Entity в Domain модель
    private fun RecipeEntity.toRecipe(): Recipe {
        return Recipe(
            id = id,
            title = title,
            timeMinutes = timeMinutes,
            category = null, // TODO: Add category field to Entity if needed
            tags = emptyList(), // TODO: Add tags if needed
            imageUrl = null,
            ingredients = emptyList(), // TODO: Load from cross-ref table
            nutrients = com.example.kursah_kotlin.data.model.Nutrients(
                calories = calories,
                protein = protein,
                fat = fat,
                carbs = carbs
            ),
            steps = emptyList(), // TODO: Add steps if needed
            isFavorite = isFavorite
        )
    }
}
