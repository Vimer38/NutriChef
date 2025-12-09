package com.example.kursah_kotlin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kursah_kotlin.data.local.dao.IngredientDao
import com.example.kursah_kotlin.data.local.dao.RecipeDao
import com.example.kursah_kotlin.data.local.entities.IngredientEntity
import com.example.kursah_kotlin.data.local.entities.RecipeEntity
import com.example.kursah_kotlin.data.local.entities.RecipeIngredientCrossRef

@Database(
    entities = [
        IngredientEntity::class,
        RecipeEntity::class,
        RecipeIngredientCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
}

