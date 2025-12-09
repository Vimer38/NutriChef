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

fun RecipeDto.toEntities(): RecipeEntitiesBundle {
    val ingredientsEntities = ingredients.map {
        IngredientEntity(
            localId = 0,
            remoteId = it.ingredient.id,
            name = it.ingredient.name
        )
    }

    val crossRefs = ingredients.mapIndexed { index, amountDto ->
        RecipeIngredientCrossRef(
            recipeId = id,
            ingredientLocalId = index.toLong(), // реальный id будет после вставки; для упрощения вставляем заглушку
            amount = amountDto.amount,
            unit = amountDto.unit
        )
    }

    val recipeEntity = RecipeEntity(
        id = id,
        title = title,
        timeMinutes = timeMinutes,
        servings = servings,
        calories = nutrients.calories,
        protein = nutrients.protein,
        fat = nutrients.fat,
        carbs = nutrients.carbs,
        micronutrientsJson = nutrients.micronutrients?.let { gson.toJson(it) },
        isFavorite = isFavorite
    )

    return RecipeEntitiesBundle(
        recipe = recipeEntity,
        ingredients = ingredientsEntities,
        crossRefs = crossRefs
    )
}

