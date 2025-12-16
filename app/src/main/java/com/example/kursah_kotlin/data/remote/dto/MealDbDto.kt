package com.example.kursah_kotlin.data.remote.dto

data class MealFilterResponse(
    val meals: List<MealShortDto>?
)

data class MealShortDto(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String?
)


