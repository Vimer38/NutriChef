package com.example.kursah_kotlin.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val age: String? = null,
    val goal: String? = null,
    val photoPath: String? = null,
    val nickname: String? = null
)



