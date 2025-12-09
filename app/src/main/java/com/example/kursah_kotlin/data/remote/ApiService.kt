package com.example.kursah_kotlin.data.remote

import com.example.kursah_kotlin.data.remote.dto.RecipeDto
import retrofit2.http.GET

interface ApiService {
    @GET("recipes")
    suspend fun getRecipes(): List<RecipeDto>
}

