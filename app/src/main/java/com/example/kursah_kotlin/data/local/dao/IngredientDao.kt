package com.example.kursah_kotlin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursah_kotlin.data.local.entities.IngredientEntity

@Dao
interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<IngredientEntity>)

    @Query("SELECT * FROM ingredients")
    suspend fun getAll(): List<IngredientEntity>
}

