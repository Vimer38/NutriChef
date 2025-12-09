package com.example.kursah_kotlin.data.remote.dto

data class IngredientDto(
    val id: String? = null,
    val name: String
)

data class IngredientAmountDto(
    val ingredient: IngredientDto,
    val amount: Double? = null,
    val unit: String? = null
)

data class NutrientsDto(
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null,
    val micronutrients: Map<String, Double>? = null
)

data class RecipeDto(
    val id: String,
    val title: String,
    val timeMinutes: Int? = null,
    val servings: Int? = null,
    val ingredients: List<IngredientAmountDto> = emptyList(),
    val nutrients: NutrientsDto = NutrientsDto(),
    val steps: List<String> = emptyList(),
    val isFavorite: Boolean = false
)

