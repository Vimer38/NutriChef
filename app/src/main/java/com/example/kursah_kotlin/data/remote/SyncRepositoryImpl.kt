package com.example.kursah_kotlin.data.remote

import com.example.kursah_kotlin.data.local.dao.IngredientDao
import com.example.kursah_kotlin.data.local.dao.RecipeDao
import com.example.kursah_kotlin.data.local.entities.IngredientEntity
import com.example.kursah_kotlin.data.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepositoryImpl(
    private val api: ApiService,
    private val ingredientDao: IngredientDao,
    private val recipeDao: RecipeDao
) : SyncRepository {

    override suspend fun syncRecipes() = withContext(Dispatchers.IO) {
        val remote = api.getRecipes()
        val bundles = remote.mapNotNull { it.toEntities() }

        val currentFavorites = recipeDao.getFavorites()
        val favoriteIds = currentFavorites.map { it.id }.toSet()

        val ingredients: List<IngredientEntity> = bundles.flatMap { it.ingredients }

        val recipes = bundles.map { bundle ->
            if (favoriteIds.contains(bundle.recipe.id)) {
                bundle.recipe.copy(isFavorite = true)
            } else {
                bundle.recipe
            }
        }
        
        val crossRefs = bundles.flatMap { it.crossRefs }

        ingredientDao.upsertAll(ingredients)
        recipeDao.upsertRecipes(recipes)
        recipeDao.upsertRecipeIngredients(crossRefs)
    }

    override suspend fun syncFavorites() {
    }

    override suspend fun syncUserData() {
    }
}

