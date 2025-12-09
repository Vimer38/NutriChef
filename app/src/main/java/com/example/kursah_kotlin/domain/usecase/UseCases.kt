package com.example.kursah_kotlin.domain.usecase

import com.example.kursah_kotlin.data.model.Ingredient
import com.example.kursah_kotlin.data.model.Recipe
import com.example.kursah_kotlin.data.repository.IngredientsRepository
import com.example.kursah_kotlin.data.repository.NutrientFilter
import com.example.kursah_kotlin.data.repository.RecipesRepository
import com.example.kursah_kotlin.data.repository.SyncRepository

data class FilterCriteria(
    val query: String? = null,
    val maxTimeMinutes: Int? = null,
    val nutrientFilter: NutrientFilter = NutrientFilter()
)

class AddIngredientsUseCase(
    private val ingredientsRepository: IngredientsRepository
) {
    suspend operator fun invoke(ingredients: List<Ingredient>) {
        ingredientsRepository.upsertUserIngredients(ingredients)
    }
}

class FindRecipesByIngredientsUseCase(
    private val recipesRepository: RecipesRepository
) {
    suspend operator fun invoke(ingredients: List<Ingredient>): List<Recipe> {
        return recipesRepository.searchRecipes(
            query = null,
            requiredIngredients = ingredients
        )
    }
}

class SearchRecipesUseCase(
    private val recipesRepository: RecipesRepository
) {
    suspend operator fun invoke(criteria: FilterCriteria): List<Recipe> {
        return recipesRepository.searchRecipes(
            query = criteria.query,
            maxTimeMinutes = criteria.maxTimeMinutes,
            requiredIngredients = emptyList(),
            nutrientFilters = criteria.nutrientFilter
        )
    }
}

class ToggleFavoriteUseCase(
    private val recipesRepository: RecipesRepository
) {
    suspend operator fun invoke(recipeId: String, isFavorite: Boolean) {
        recipesRepository.toggleFavorite(recipeId, isFavorite)
    }
}

class SyncRecipesUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke() {
        syncRepository.syncRecipes()
    }
}

class SyncFavoritesUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke() {
        syncRepository.syncFavorites()
    }
}

