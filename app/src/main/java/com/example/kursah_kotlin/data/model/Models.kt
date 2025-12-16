package com.example.kursah_kotlin.data.model

data class Ingredient(
    val id: String? = null,
    val name: String
)

data class IngredientAmount(
    val ingredient: Ingredient,
    val amount: Double? = null,
    val unit: String? = null
)

data class Nutrients(
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null,
    val fatSaturated: Double? = null,
    val sugar: Double? = null,
    val fiber: Double? = null,
    val sodium: Double? = null
)

data class Recipe(
    val id: String,
    val title: String,
    val timeMinutes: Int? = null,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val imageUrl: String? = null,
    val ingredients: List<IngredientAmount> = emptyList(),
    val nutrients: Nutrients = Nutrients(),
    val steps: List<String> = emptyList(),
    val isFavorite: Boolean = false
)

data class UserProfile(
    val id: String,
    val name: String? = null,
    val goals: List<String> = emptyList()
)

