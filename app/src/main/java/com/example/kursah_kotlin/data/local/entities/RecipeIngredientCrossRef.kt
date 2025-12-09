package com.example.kursah_kotlin.data.local.entities

import androidx.room.Entity

/**
 * Связка рецепт – ингредиент, с количеством.
 */
@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = ["recipeId", "ingredientLocalId"]
)
data class RecipeIngredientCrossRef(
    val recipeId: String,
    val ingredientLocalId: Long,
    val amount: Double? = null,
    val unit: String? = null
)

