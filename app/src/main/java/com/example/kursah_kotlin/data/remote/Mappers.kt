package com.example.kursah_kotlin.data.remote

import com.example.kursah_kotlin.data.local.entities.IngredientEntity
import com.example.kursah_kotlin.data.local.entities.RecipeEntity
import com.example.kursah_kotlin.data.local.entities.RecipeIngredientCrossRef
import com.example.kursah_kotlin.data.remote.dto.IngredientAmountDto
import com.example.kursah_kotlin.data.remote.dto.RecipeDto
import com.google.gson.Gson

data class RecipeEntitiesBundle(
    val recipe: RecipeEntity,
    val ingredients: List<IngredientEntity>,
    val crossRefs: List<RecipeIngredientCrossRef>
)

private val gson = Gson()

fun RecipeDto.toEntities(): RecipeEntitiesBundle? {
    val nonNullId = id ?: return null
    val nonNullTitle = title ?: return null

    val validIngredients = ingredients.filter { it.ingredient?.name != null }

    val ingredientsEntities = validIngredients.map {
        IngredientEntity(
            localId = 0,
            remoteId = it.ingredient?.id,
            name = it.ingredient?.name ?: "" // Should not happen due to filter
        )
    }

    val crossRefs = validIngredients.mapIndexed { index, amountDto ->
        RecipeIngredientCrossRef(
            recipeId = nonNullId,
            ingredientLocalId = index.toLong(),
            amount = amountDto.amount,
            unit = amountDto.unit
        )
    }

    val recipeEntity = RecipeEntity(
        id = nonNullId,
        title = nonNullTitle,
        description = description,
        rating = rating,
        timeMinutes = timeMinutes,
        calories = nutrients?.calories,
        protein = nutrients?.protein,
        fat = nutrients?.fat,
        carbs = nutrients?.carbs,
        micronutrientsJson = null,
        isFavorite = isFavorite,
        isRecipeOfWeek = isRecipeOfWeek
    )

    return RecipeEntitiesBundle(
        recipe = recipeEntity,
        ingredients = ingredientsEntities,
        crossRefs = crossRefs
    )
}

