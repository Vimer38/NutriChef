package com.example.kursah_kotlin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursah_kotlin.data.local.entities.RecipeEntity
import com.example.kursah_kotlin.data.local.entities.RecipeIngredientCrossRef

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipes(recipes: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipeIngredients(links: List<RecipeIngredientCrossRef>)

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    suspend fun getFavorites(): List<RecipeEntity>

    @Query(
        """
        UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId
        """
    )
    suspend fun setFavorite(recipeId: String, isFavorite: Boolean)

    @Query(
        """
        SELECT * FROM recipes
        WHERE (:query IS NULL OR title LIKE '%' || :query || '%')
        AND (:maxTime IS NULL OR (timeMinutes IS NOT NULL AND timeMinutes <= :maxTime))
        """
    )
    suspend fun searchBasic(query: String?, maxTime: Int?): List<RecipeEntity>
}

