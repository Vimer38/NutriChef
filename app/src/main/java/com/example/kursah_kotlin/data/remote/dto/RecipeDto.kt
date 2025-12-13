package com.example.kursah_kotlin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class IngredientDto(
    val id: String? = null,
    val name: String? = null
)

data class IngredientAmountDto(
    val ingredient: IngredientDto? = null,
    val amount: Double? = null,
    val unit: String? = null
)

data class NutrientsDto(
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    @SerializedName("carbohydrates")
    val carbs: Double? = null,
    val fatSaturated: Double? = null,
    val sugar: Double? = null,
    val fiber: Double? = null,
    val sodium: Double? = null
)

data class RecipeDto(
    val id: String? = null,
    val title: String? = null,
    val timeMinutes: Int? = null,
    val category: String? = null,
    val difficulty: String? = null,
    val description: String? = null,
    @SerializedName("rating")
    val rating: Double? = null,
    val imageUrl: String? = null,
    val ingredients: List<IngredientAmountDto> = emptyList(),
    val nutrients: NutrientsDto? = null,
    val steps: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isRecipeOfWeek: Boolean = false
)

