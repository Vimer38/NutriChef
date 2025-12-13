package com.example.kursah_kotlin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val rating: Double?,
    val timeMinutes: Int?,
    val calories: Int?,
    val protein: Double?,
    val fat: Double?,
    val carbs: Double?,
    val micronutrientsJson: String?, // для map<String,Double> или других данных
    val isFavorite: Boolean = false,
    val isRecipeOfWeek: Boolean = false
)

