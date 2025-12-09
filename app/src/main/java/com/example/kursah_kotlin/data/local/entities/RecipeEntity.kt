package com.example.kursah_kotlin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val timeMinutes: Int?,
    val servings: Int?,
    val calories: Int?,
    val protein: Double?,
    val fat: Double?,
    val carbs: Double?,
    val micronutrientsJson: String?, // для map<String,Double>
    val isFavorite: Boolean = false
)

