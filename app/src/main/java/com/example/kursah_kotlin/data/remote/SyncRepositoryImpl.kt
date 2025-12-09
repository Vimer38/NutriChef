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
        val bundles = remote.map { it.toEntities() }

        // Наивная вставка: ингредиенты без дедупликации.
        // Для реального кейса нужна нормализация (find by name/remoteId).
        val ingredients: List<IngredientEntity> = bundles.flatMap { it.ingredients }
        val recipes = bundles.map { it.recipe }
        val crossRefs = bundles.flatMap { it.crossRefs }

        ingredientDao.upsertAll(ingredients)
        recipeDao.upsertRecipes(recipes)
        recipeDao.upsertRecipeIngredients(crossRefs)
    }

    override suspend fun syncFavorites() {
        // TODO: реализовать обмен избранным (push/pull с отметкой времени)
    }

    override suspend fun syncUserData() {
        // TODO: профиль и пользовательские ингредиенты
    }
}

