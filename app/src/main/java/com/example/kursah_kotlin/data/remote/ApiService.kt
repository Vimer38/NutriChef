package com.example.kursah_kotlin.data.remote

import com.example.kursah_kotlin.data.remote.dto.MealFilterResponse
import com.example.kursah_kotlin.data.remote.dto.RecipeDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("recipes")
    suspend fun getRecipes(): List<RecipeDto>

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealFilterResponse
}

